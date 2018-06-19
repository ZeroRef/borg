package org.zeroref.borg.timeouts.domain;

public class PartialRefundExpiredTimeout {
    private String id;

    public PartialRefundExpiredTimeout(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
