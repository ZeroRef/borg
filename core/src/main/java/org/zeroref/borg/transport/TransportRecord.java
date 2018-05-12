package org.zeroref.borg.transport;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TransportRecord {
    private final UUID uuid;
    private final Map<String, Object> headers;
    private final Map<String, Object> content;

    public TransportRecord(UUID uuid, Map<String, Object> content, Map<String, Object> headers) {
        this.uuid = uuid;
        this.content = content;
        this.headers = headers;
    }

    public TransportRecord(UUID uuid, Map<String, Object> content) {
        this(uuid, content, new HashMap<>());
    }

    public UUID getUuid() {
        return uuid;
    }

    public Map<String, Object> getContent() {
        return content;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }
}
