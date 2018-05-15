package org.zeroref.borg.sample;

import org.testng.annotations.Test;
import org.zeroref.borg.MessageBus;
import org.zeroref.borg.runtime.EndpointWire;
import org.zeroref.borg.sample.barista.BaristaSaga;
import org.zeroref.borg.sample.barista.DrinkReady;
import org.zeroref.borg.sample.cashier.*;
import org.zeroref.borg.sample.customer.CustomerController;

import java.util.UUID;

public class SampleApp  {

    @Test
    public void end2end() throws Exception {

        String kfk = "localhost:9092";
        String zk = "localhost:2181";
        try(
                EndpointWire cashier = new EndpointWire("cashier", kfk, zk);
                EndpointWire barista = new EndpointWire("barista", kfk, zk);
                EndpointWire customer = new EndpointWire("customer", kfk, zk)
        ){
            CashierSaga cashierSaga = new CashierSaga();
            cashier.registerHandler(NewOrder.class, b -> m -> cashierSaga.handle(b, m));
            cashier.registerHandler(SubmitPayment.class, b -> m -> cashierSaga.handle(b, m));
            cashier.configure();

            BaristaSaga baristaSaga = new BaristaSaga();
            barista.subscribeToEndpoint("cashier", PrepareDrink.class, PaymentComplete.class);
            barista.registerHandler(PrepareDrink.class, b -> m -> baristaSaga.handle(b, m));
            barista.registerHandler(PaymentComplete.class, b -> m -> baristaSaga.handle(b, m));
            barista.configure();

            CustomerController controller = new CustomerController();
            customer.registerEndpointRoute("cashier", NewOrder.class);
            customer.subscribeToEndpoint("barista", DrinkReady.class);
            customer.registerHandler(DrinkReady.class, b -> controller::handle);
            customer.registerHandler(PaymentDue.class, b -> m -> controller.handle(b, m));
            customer.configure();

            Thread.sleep(500);

            controller.buyMeADrink(customer.getMessageBus());

            Thread.sleep(8000);
        }
    }
}
