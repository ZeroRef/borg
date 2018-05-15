package org.zeroref.borg.sample.cashier;

import java.util.UUID;

public class SubmitPayment
{
    public SubmitPayment(UUID correlationId, double amount) {
        this.correlationId = correlationId;
        this.amount = amount;
    }

    public UUID correlationId;
    public double amount;
}
