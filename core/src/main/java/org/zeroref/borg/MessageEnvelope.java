package org.zeroref.borg;

import java.util.Map;
import java.util.UUID;

public class MessageEnvelope {
    private final UUID uuid;
    private final String returnAddress;
    private final Map<String, Object> headers;
    private final Object localMessage;
    private Object offset;

    public MessageEnvelope(UUID uuid, String returnAddress, Map<String, Object> headers, Object localMessage) {

        this.uuid = uuid;
        this.returnAddress = returnAddress;
        this.headers = headers;
        this.localMessage = localMessage;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getReturnAddress() {
        return returnAddress;
    }

    public Map<String, Object> getHeaders() {
        return headers;
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
