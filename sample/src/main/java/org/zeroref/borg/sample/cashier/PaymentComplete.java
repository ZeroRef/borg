package org.zeroref.borg.sample.cashier;

import java.util.UUID;

public class PaymentComplete
{
    public PaymentComplete(String correlationId) {
        this.correlationId = correlationId;
    }

    public String correlationId;
}
