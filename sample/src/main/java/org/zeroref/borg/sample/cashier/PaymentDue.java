package org.zeroref.borg.sample.cashier;

import java.util.UUID;

public class PaymentDue
{
    public PaymentDue(String customerName, UUID transactionId, double amount) {
        this.customerName = customerName;
        this.transactionId = transactionId;
        this.amount = amount;
    }

    public String customerName;
    public UUID transactionId;
    public double amount;
}
