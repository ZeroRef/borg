package org.zeroref.borg.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroref.borg.*;
import org.zeroref.borg.directions.MessageDestinations;
import org.zeroref.borg.runtime.EndpointId;
import org.zeroref.borg.sagas.SagaPersistence;
import org.zeroref.borg.transport.KafkaMessageSender;


public class MessagePipeline implements DispatchMessagesToHandlers {

    private MessageHandlerTable handlers;
    private KafkaMessageSender sender;
    private EndpointId endpointId;
    private MessageDestinations router;
    private SagaPersistence sagaPersistence;
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagePipeline.class);

    public MessagePipeline(MessageHandlerTable handlers, KafkaMessageSender sender, EndpointId endpointId, MessageDestinations router, SagaPersistence sagaPersistence) {
        this.handlers = handlers;
        this.sender = sender;
        this.endpointId = endpointId;
        this.router = router;
        this.sagaPersistence = sagaPersistence;
    }

    @Override
    public void dispatch(MessageEnvelope message) {
        MessageBus slim = netMessageBus(message);
        UnitOfWork unitOfWork = new UnitOfWork();
        TransactionalMessageBus messageBus = new TransactionalMessageBus(slim, unitOfWork);

        Object localMessage = message.getLocalMessage();

        if(this.handlers.isHandlerSubscription(localMessage)){
            HandleMessages<Object> handler = this.handlers.getHandlers(messageBus, localMessage);
            handler.handle(localMessage);
        }else if(this.sagaPersistence.isSagaSubscription(localMessage)){
            sagaPersistence.dispatch(localMessage);
        }else {
            String simpleName = localMessage.getClass().toString();
            LOGGER.warn("No handler or saga registered for {}", simpleName);
        }

        unitOfWork.complete();
    }

    public MessageBus netMessageBus(MessageEnvelope message) {
        return new UnicastMessageBus(sender, message, endpointId, router);
    }
}
