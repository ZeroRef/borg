package org.zeroref.borg.sample.cashier;

import java.util.UUID;

public class PaymentComplete
{
    public PaymentComplete(UUID correlationId) {
        this.correlationId = correlationId;
    }

    public UUID correlationId;
}
