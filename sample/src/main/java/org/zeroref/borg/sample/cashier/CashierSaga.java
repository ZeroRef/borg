package org.zeroref.borg.sample.cashier;

import org.zeroref.borg.MessageBus;

import java.util.UUID;

public class CashierSaga {
    public void handle(MessageBus bus, NewOrder message){
        System.out.println("Cashier: got new order");
        UUID correlationId = UUID.randomUUID();

        bus.publish(new PrepareDrink(
                message.drinkName,
                message.size,
                message.customerName,
                correlationId
        ));

        bus.reply(new PaymentDue(
                message.customerName,
                correlationId,
                message.size * 1.25
        ));
    }

    public void handle(MessageBus bus, SubmitPayment message){
        System.out.println("Cashier: got payment");
        bus.publish(new PaymentComplete(message.correlationId));
    }
}
