package org.zeroref.borg.sample.cashier;

import java.util.UUID;

public class PrepareDrink
{
    public String drinkName;
    public int size;
    public String customerName;
    public String correlationId;

    public PrepareDrink(String drinkName, int size, String customerName, String correlationId) {
        this.drinkName = drinkName;
        this.size = size;
        this.customerName = customerName;
        this.correlationId = correlationId;
    }
}
