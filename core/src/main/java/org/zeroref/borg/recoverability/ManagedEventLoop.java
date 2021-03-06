package org.zeroref.borg.recoverability;

import org.apache.kafka.common.errors.InterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroref.borg.MessageEnvelope;
import org.zeroref.borg.pipeline.DispatchMessagesToHandlers;
import org.zeroref.borg.transport.KafkaMessageReceiver;

import java.io.Closeable;
import java.util.List;

public class ManagedEventLoop implements Closeable {
    private String name;
    private final String kafka;
    private final List<String> inputTopics;
    private DispatchMessagesToHandlers router;
    private Thread worker;

    private Object syncObject = new Object();

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagedEventLoop.class);

    public ManagedEventLoop(String name, String kafka, List<String> inputTopics, DispatchMessagesToHandlers router) {
        this.name = name;
        this.kafka = kafka;
        this.inputTopics = inputTopics;
        this.router = router;
    }

    public void start(){
        if(inputTopics.isEmpty())
            throw new RuntimeException("Subscription with 0 topics is not allowed for " + name);

        this.worker = new Thread(this::StartReceiving,name);
        this.worker.setPriority(Thread.MAX_PRIORITY);
        this.worker.start();

        synchronized(syncObject) {
            try {
                syncObject.wait();
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public void close(){
        worker.interrupt();
    }

    public void StartReceiving() {
        KafkaMessageReceiver receiver = new KafkaMessageReceiver(kafka, name, inputTopics);
        receiver.start();

        synchronized(syncObject) {
            syncObject.notify();
            LOGGER.info("started loop");
        }

        while (true){
            try {
                MessageEnvelope env = receiver.receive();

                LOGGER.debug("Poll");

                if(env == null)
                    continue;

                LOGGER.debug("Dispatch to router {}", router.getClass().getSimpleName());
                router.dispatch(env);
                receiver.commit(env);
                LOGGER.debug("Dispatch completed");

            } catch (InterruptException e) {
                safeStop(receiver);
                LOGGER.info("stopped loop");
                break;
            } catch (Exception e) {
                LOGGER.error("Message processing failed", e);
            }
        }
    }

    private void safeStop(KafkaMessageReceiver receiver) {
        try{
            receiver.stop();
        }catch (Exception ex){

        }
    }
}
