package org.zeroref.borg.timeouts.domain;

public class OrderShipped {
    private String id;

    public OrderShipped(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
