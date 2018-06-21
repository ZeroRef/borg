package org.zeroref.borg.timeouts;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.zeroref.borg.lab.Env;
import org.zeroref.borg.lab.Msg;
import org.zeroref.borg.runtime.EndpointWire;
import org.zeroref.borg.sagas.domain.MiniSagaState;
import org.zeroref.borg.timeouts.domain.CancelOrder;
import org.zeroref.borg.timeouts.domain.OrderBilled;
import org.zeroref.borg.timeouts.domain.OrderCancellationPolicy;
import org.zeroref.borg.timeouts.domain.OrderShipped;

public class TimeoutWiredTest extends Env {

    @Test
    public void timeout() throws Exception {

        try(EndpointWire wire = wire("order-pm-policy")){
            wire.subscribeToEndpoint("billing", OrderBilled.class);
            wire.subscribeToEndpoint("shipping", OrderShipped.class);
            wire.registerSagas(OrderCancellationPolicy.class);
            wire.configure();

            send("billing.events", Msg.fromInstance(new OrderBilled("o1")));

            Thread.sleep(3000);

            send("order-pm-policy", Msg.fromInstance(new CancelOrder("o1")));


            Assert.assertEquals(countMessages("order-pm-policy.events"), 1);
        }
    }
}
