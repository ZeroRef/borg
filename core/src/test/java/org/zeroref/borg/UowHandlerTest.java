package org.zeroref.borg;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.zeroref.borg.lab.Env;
import org.zeroref.borg.lab.Msg;
import org.zeroref.borg.pipeline.HandleMessages;
import org.zeroref.borg.runtime.EndpointWire;

public class UowHandlerTest extends Env {

    public class PingUowHandler implements HandleMessages<Msg.Ping> {
        private MessageBus bus;

        public PingUowHandler(MessageBus bus) {
            this.bus = bus;
        }

        @Override
        public void handle(Msg.Ping message) {
            bus.publish(new Msg.Pong());

            throw new RuntimeException("this should make unit of work abort");
        }
    }

    @Test
    public void exceptions_isolates_side_effects() throws Exception {

        try(EndpointWire wire = wire("uow")){
            wire.registerHandler(Msg.Ping.class, bus -> new PingUowHandler(bus));
            wire.configure();

            send("uow", Msg.fromInstance(new Msg.Ping()));

            Assert.assertEquals(countMessages("uow.events"), 0);
            Assert.assertEquals(countMessages("uow.errors"), 1);
        }
    }
}
