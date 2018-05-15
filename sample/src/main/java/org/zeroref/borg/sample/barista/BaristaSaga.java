package org.zeroref.borg.sample.barista;

import org.zeroref.borg.MessageBus;
import org.zeroref.borg.sample.SampleApp;
import org.zeroref.borg.sample.cashier.PaymentComplete;
import org.zeroref.borg.sample.cashier.PrepareDrink;

import java.util.UUID;

public class BaristaSaga {

    private String drink;
    private UUID correlationId;
    private boolean drinkIsReady;
    private boolean gotPayment;

    public void handle(MessageBus bus, PrepareDrink message)
    {
        drink = message.drinkName;
        correlationId = message.correlationId;

        for (int i = 0; i < 10; i++)
        {
            System.out.println("Barista: preparing drink: " + drink);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        drinkIsReady = true;
        SubmitOrderIfDone(bus);
    }

    public void handle(MessageBus bus, PaymentComplete message)
    {
        System.out.println("Barista: got payment notification");
        gotPayment = true;
        SubmitOrderIfDone(bus);
    }

    private void SubmitOrderIfDone(MessageBus bus)
    {
        if (gotPayment && drinkIsReady)
        {
            System.out.println("Barista: drink is ready");
            bus.publish(new DrinkReady(correlationId, drink));
        }
    }
}
