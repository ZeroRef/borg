package org.zeroref.borg.sample.cashier;

import java.util.UUID;

////////// cashier
public class NewOrder
{
    public String correlationId;

    public NewOrder(String drinkName, int size, String customerName, String correlationId) {
        this.drinkName = drinkName;
        this.size = size;
        this.customerName = customerName;
        this.correlationId = correlationId;
    }

    public String drinkName;
    public int size ;
    public String customerName;
}
