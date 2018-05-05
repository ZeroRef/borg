package org.zeroref.borg;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.zeroref.borg.lab.Env;
import org.zeroref.borg.lab.Msg;
import org.zeroref.borg.runtime.EndpointWire;

import java.util.concurrent.atomic.AtomicInteger;

public class RecoveryTest extends Env {
    @Test
    public void recovery_will_give_up_after_flr_slr() throws Exception {

        AtomicInteger cnt = new AtomicInteger();

        try(EndpointWire wire = wire("manual-review")){
            wire.registerHandler(Msg.Ping.class, bus -> message -> {
                cnt.incrementAndGet();
                throw new RuntimeException("intentional");
            });
            wire.configure();

            send("manual-review", Msg.from(Msg.Ping.class));

            Thread.sleep(6000);

            Assert.assertEquals(cnt.get(), 5);
            Assert.assertEquals(countMessages("manual-review.errors"), 1);
        }
    }

    @Test
    public void recovery_will_succeed_in_slr() throws Exception {

        try(EndpointWire wire = wire("recover-in-slr")){
            AtomicInteger cnt = new AtomicInteger();

            wire.registerHandler(Msg.Ping.class, bus -> message -> {
                if(cnt.getAndIncrement() < 4) throw new RuntimeException("intentional");
            });
            wire.configure();

            send("recover-in-slr", Msg.from(Msg.Ping.class));

            Thread.sleep(6000);

            Assert.assertEquals(cnt.get(), 5);
            Assert.assertEquals(countMessages("recover-in-slr.errors"), 0);
        }
    }

    @Test
    public void recovery_will_succeed_in_flr() throws Exception {

        try(EndpointWire wire = wire("recover-in-flr")){
            AtomicInteger cnt = new AtomicInteger();

            wire.registerHandler(Msg.Ping.class, bus -> message -> {
                if(cnt.getAndIncrement() < 1) throw new RuntimeException("intentional");
            });
            wire.configure();

            send("recover-in-flr", Msg.from(Msg.Ping.class));

            Thread.sleep(4000);

            Assert.assertEquals(cnt.get(), 2);
            Assert.assertEquals(countMessages("recover-in-flr.errors"), 0);
        }
    }
}
