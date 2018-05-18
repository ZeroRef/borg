package org.zeroref.borg.sample;

import org.testng.annotations.Test;
import org.zeroref.borg.MessageBus;
import org.zeroref.borg.runtime.EndpointWire;
import org.zeroref.borg.sample.barista.BaristaSaga;
import org.zeroref.borg.sample.barista.DrinkReady;
import org.zeroref.borg.sample.cashier.*;
import org.zeroref.borg.sample.customer.CustomerController;

public class SampleApp  {

    @Test
    public void end2end() throws Exception {

        String kfk = "localhost:9092";
        String zk = "localhost:2181";
        try(
                EndpointWire cashier = new EndpointWire("cashier2", kfk, zk);
                EndpointWire barista = new EndpointWire("barista2", kfk, zk);
                EndpointWire customer = new EndpointWire("customer2", kfk, zk)
        ){
            cashier.registerSagas(CashierSaga.class);
            cashier.configure();

            barista.subscribeToEndpoint("cashier2", PrepareDrink.class, PaymentComplete.class);
            barista.registerSagas(BaristaSaga.class);
            barista.configure();

            CustomerController controller = new CustomerController();
            customer.registerEndpointRoute("cashier2", NewOrder.class);
            customer.subscribeToEndpoint("barista2", DrinkReady.class);
            customer.registerHandler(DrinkReady.class, b -> controller::handle);
            customer.registerHandler(PaymentDue.class, b -> m -> controller.handle(b, m));
            customer.configure();

            Thread.sleep(500);

            controller.buyMeADrink(customer.getMessageBus());

            Thread.sleep(8000);
        }
    }
}
