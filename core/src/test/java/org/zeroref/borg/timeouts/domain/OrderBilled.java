package org.zeroref.borg.timeouts.domain;

public class OrderBilled {
    private String id;

    public OrderBilled(String id) {

        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
