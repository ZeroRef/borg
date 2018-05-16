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
                EndpointWire cashier = new EndpointWire("cashier1", kfk, zk);
                EndpointWire barista = new EndpointWire("barista1", kfk, zk);
                EndpointWire customer = new EndpointWire("customer1", kfk, zk)
        ){
            CashierSaga cashierSaga = new CashierSaga();
            cashier.registerSagas(CashierSaga.class);
            cashier.configure();

            BaristaSaga baristaSaga = new BaristaSaga();
            barista.subscribeToEndpoint("cashier1", PrepareDrink.class, PaymentComplete.class);
            barista.registerSagas(BaristaSaga.class);
            barista.configure();

            CustomerController controller = new CustomerController();
            customer.registerEndpointRoute("cashier1", NewOrder.class);
            customer.subscribeToEndpoint("barista1", DrinkReady.class);
            customer.registerHandler(DrinkReady.class, b -> controller::handle);
            customer.registerHandler(PaymentDue.class, b -> m -> controller.handle(b, m));
            customer.configure();

            Thread.sleep(500);

            controller.buyMeADrink(customer.getMessageBus());

            Thread.sleep(8000);
        }
    }
}
