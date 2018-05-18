package org.zeroref.borg;

import java.util.Map;
import java.util.UUID;

public class MessageEnvelope {
    private final UUID uuid;
    private final String returnAddress;
    private final Object localMessage;
    private Object offset;

    public MessageEnvelope(UUID uuid, String returnAddress, Object localMessage) {

        this.uuid = uuid;
        this.returnAddress = returnAddress;
        this.localMessage = localMessage;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getReturnAddress() {
        return returnAddress;
    }

    public Object getLocalMessage() {
        return localMessage;
    }

    public void setOffset(Object offset) {
        this.offset = offset;
    }

    public Object getOffset() {
        return offset;
    }
}
