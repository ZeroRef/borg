package org.zeroref.borg.sample.cashier;

////////// cashier
public class NewOrder
{
    public NewOrder(String drinkName, int size, String customerName) {
        this.drinkName = drinkName;
        this.size = size;
        this.customerName = customerName;
    }

    public String drinkName;
    public int size ;
    public String customerName;
}
