package org.zeroref.borg.sample.cashier;

import java.util.UUID;

public class SubmitPayment
{
    public SubmitPayment(String correlationId, double amount) {
        this.correlationId = correlationId;
        this.amount = amount;
    }

    public String correlationId;
    public double amount;
}
