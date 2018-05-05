package org.zeroref.borg.sagas.domain;

public class OrderPlaced {
    String orderId;

    public OrderPlaced(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

}
