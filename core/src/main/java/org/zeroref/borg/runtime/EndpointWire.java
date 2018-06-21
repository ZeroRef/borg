package org.zeroref.borg.runtime;

import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroref.borg.MessageBus;
import org.zeroref.borg.diagnostics.ConfigurationInspector;
import org.zeroref.borg.directions.MessageDestinations;
import org.zeroref.borg.directions.MessageSubscriptions;
import org.zeroref.borg.pipeline.HandleMessages;
import org.zeroref.borg.pipeline.MessageHandlerTable;
import org.zeroref.borg.pipeline.MessagePipeline;
import org.zeroref.borg.recoverability.*;
import org.zeroref.borg.sagas.SagaBase;
import org.zeroref.borg.sagas.SagaPersistence;
import org.zeroref.borg.sagas.SagaStorage;
import org.zeroref.borg.timeouts.InMemoryTimeoutManager;
import org.zeroref.borg.timeouts.TimeoutManager;
import org.zeroref.borg.transport.KafkaMessageSender;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

public class EndpointWire implements Closeable{
    private final EndpointId endpointId;
    private final String kafkaConnection;
    private final KafkaMessageSender sender;
    private final String zookeeper;
    private final MessageHandlerTable table = new MessageHandlerTable();
    private final MessageDestinations router = new MessageDestinations();
    private final MessageSubscriptions subscriptions = new MessageSubscriptions();
    private final MessagePipeline pipeline;
    private ManagedEventLoop inputEventLoop;
    private ManagedEventLoop slrEventLoop;
    private ManagedEventLoop subscriptionsEventLoop;
    private ManagedEventLoop timeoutsEventLoop;
    private final List<String> inputTopics;
    private final SagaPersistence sagaPersistence = new SagaPersistence(new SagaStorage());
    private final TimeoutManager timeouts = new InMemoryTimeoutManager();

    private final BackOff flrBackoff = new BackOff(100L);
    private final BackOff slrBackoff = new BackOff(1500L);

    private final ConfigurationInspector inspector;

    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingEventLoop.class);

    public EndpointWire(String endpoint,String kafkaConnection, String zookeeper) {
        this.endpointId = new EndpointId(endpoint);
        this.kafkaConnection = kafkaConnection;
        this.sender = new KafkaMessageSender(kafkaConnection);
        this.zookeeper = zookeeper;
        this.pipeline = new MessagePipeline(table, sender, endpointId, router, sagaPersistence, timeouts);
        this.inputTopics = Arrays.asList(endpointId.getInputTopicName());
        this.inspector = new ConfigurationInspector(endpointId);
    }

    public void configure(){

        inspector.inspectSagas(sagaPersistence);
        inspector.inspectHandlers(table);
        inspector.inspectSubscriptions(subscriptions);
        inspector.inspectRouting(router);
        inspector.inspectFlr(flrBackoff);
        inspector.inspectSlr(slrBackoff);

        inspector.present();

        createTopic(endpointId.getInputTopicName(), 1, 1, new Properties());
        createTopic(endpointId.getEventsTopicName(), 1, 1, new Properties());
        createTopic(endpointId.getSlrTopicName(), 1, 1, new Properties());
        createTopic(endpointId.getErrorsTopicName(), 1, 1, new Properties());

        subscriptionsEventLoop = newMessagingLoopWithSlr(endpointId.getInputTopicName() + "-sub", subscriptions.sources());
        inputEventLoop = newMessagingLoopWithSlr(endpointId.getInputTopicName()+ "-in", inputTopics);

        slrEventLoop = newMessagingWithError(endpointId.getInputTopicName()+ "-slr", Arrays.asList(endpointId.getSlrTopicName()));

        timeoutsEventLoop = newTimeoutWithError();

        if(!subscriptions.sources().isEmpty())
            subscriptionsEventLoop.start();

        timeoutsEventLoop.start();

        inputEventLoop.start();
        slrEventLoop.start();
        sender.start();
    }

    @Override
    public void close() throws IOException {
        if(!subscriptions.sources().isEmpty())
            subscriptionsEventLoop.close();

        timeoutsEventLoop.close();

        inputEventLoop.close();
        slrEventLoop.close();
        sender.stop();
    }

    private ManagedEventLoop newMessagingLoopWithSlr(String name, List<String> topics){
        Dispatcher forwardSlr = Dispatcher.withSrl(pipeline, sender, endpointId, flrBackoff);
        return new MessagingEventLoop(name, kafkaConnection, topics, forwardSlr);
    }

    private ManagedEventLoop newMessagingWithError(String name, List<String> topics){
        Dispatcher forwardError = Dispatcher.withError(pipeline, sender, endpointId, slrBackoff);
        return new MessagingEventLoop(name, kafkaConnection, topics, forwardError);
    }

    private ManagedEventLoop newTimeoutWithError(){
        Dispatcher forwardError = Dispatcher.withError(pipeline, sender, endpointId, slrBackoff);
        return new TimeoutsEventLoop(timeouts, forwardError);
    }

    public void registerEndpointRoute(String endpointId, Class<?> ... types) {
        router.registerEndpoint(endpointId, types);
    }

    public void subscribeToEndpoint(String endpointId, Class<?> ... types) {
        subscriptions.subscribeToEndpoint(endpointId, types);
    }

    public <T> void registerHandler(Class<T> c, Function<MessageBus, HandleMessages<T>> handler) {
        table.registerHandler(c, handler);
    }

    public void registerSagas(Class<? extends SagaBase> ... sagaTypes){
        sagaPersistence.register(sagaTypes);
    }

    public MessageBus getMessageBus() {
        return pipeline.netMessageBus(null);
    }

    private static final int DEFAULT_ZK_SESSION_TIMEOUT_MS = 10 * 1000;
    private static final int DEFAULT_ZK_CONNECTION_TIMEOUT_MS = 8 * 1000;

    public void createTopic(String topic,
                            int partitions,
                            int replication,
                            Properties topicConfig) {
        LOGGER.debug("Creating topic { name: {}, partitions: {}, replication: {}, config: {} }",
                topic, partitions, replication, topicConfig);
        ZkClient zkClient = new ZkClient(
                zookeeper,
                DEFAULT_ZK_SESSION_TIMEOUT_MS,
                DEFAULT_ZK_CONNECTION_TIMEOUT_MS,
                ZKStringSerializer$.MODULE$);
        boolean isSecure = false;
        ZkUtils zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeper), isSecure);

        if(!AdminUtils.topicExists(zkUtils, topic)){
            AdminUtils.createTopic(zkUtils, topic, partitions, replication, topicConfig, RackAwareMode.Enforced$.MODULE$);
        }

        zkClient.close();
    }
}
