package org.zeroref.borg;

import java.util.UUID;

public class MessageBuilder {
    private String localAddress;

    public MessageBuilder(String localAddress) {
        this.localAddress = localAddress;
    }

    public MessageEnvelope buildMessage(Object message) {
        return new MessageEnvelope(
                UUID.randomUUID(),
                localAddress,
                message
        );
    }
}
