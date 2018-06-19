package org.zeroref.borg.timeouts.domain;

public class CancelOrder {
    private String id;

    public CancelOrder(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
