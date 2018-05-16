package org.zeroref.borg.sample.customer;

import org.zeroref.borg.MessageBus;
import org.zeroref.borg.sample.barista.DrinkReady;
import org.zeroref.borg.sample.cashier.NewOrder;
import org.zeroref.borg.sample.cashier.PaymentDue;
import org.zeroref.borg.sample.cashier.SubmitPayment;

import java.util.UUID;

////////// customer
public class CustomerController {

    public void handle(MessageBus bus, PaymentDue message) {
        bus.reply(new SubmitPayment(
                message.transactionId,
                message.amount)
        );
    }

    public void handle(DrinkReady message) {
        System.out.println("yay here is my latte");
    }

    public void buyMeADrink(MessageBus bus) {
        NewOrder message = new NewOrder("latte", 3, "ruslan", UUID.randomUUID().toString());
        bus.send(message);
    }
}
