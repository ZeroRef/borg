package org.zeroref.borg.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroref.borg.*;
import org.zeroref.borg.directions.MessageDestinations;
import org.zeroref.borg.runtime.EndpointId;
import org.zeroref.borg.transport.KafkaMessageSender;


public class MessagePipeline implements DispatchMessagesToHandlers {

    private MessageHandlerTable handlers;
    private KafkaMessageSender sender;
    private EndpointId endpointId;
    private MessageDestinations router;
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagePipeline.class);

    public MessagePipeline(MessageHandlerTable handlers, KafkaMessageSender sender, EndpointId endpointId, MessageDestinations router) {
        this.handlers = handlers;
        this.sender = sender;
        this.endpointId = endpointId;
        this.router = router;
    }

    @Override
    public void dispatch(MessageEnvelope message) {
        MessageBus slim = netMessageBus(message);
        UnitOfWork unitOfWork = new UnitOfWork();
        TransactionalMessageBus messageBus = new TransactionalMessageBus(slim, unitOfWork);

        HandleMessages<Object> handler = this.handlers.getHandlers(messageBus, message.getLocalMessage());

        if(handler == null){
            String simpleName = message.getLocalMessage().getClass().toString();
            LOGGER.warn("No handler registered for {}", simpleName);
        }else
            handler.handle(message.getLocalMessage());

        unitOfWork.complete();
    }


    public MessageBus netMessageBus(MessageEnvelope message) {
        return new UnicastMessageBus(sender, message, endpointId, router);
    }
}
