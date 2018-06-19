package org.zeroref.borg.timeouts.domain;

public class FullRefundExpiredTimeout {
    private String id;

    public FullRefundExpiredTimeout(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
