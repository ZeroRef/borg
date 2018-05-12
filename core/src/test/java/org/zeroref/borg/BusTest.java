package org.zeroref.borg;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.zeroref.borg.lab.Env;
import org.zeroref.borg.lab.Msg;
import org.zeroref.borg.runtime.EndpointWire;

import java.util.concurrent.atomic.AtomicInteger;

public class BusTest extends Env {

    @Test
    public void publish_will_emit_event_test() throws Exception {

        try(EndpointWire wire = wire("new-york-times")){
            wire.configure();

            MessageBus bus = wire.getMessageBus();
            bus.publish(new Msg.Ping());

            Assert.assertEquals(countMessages("new-york-times.events"), 1);
        }
    }


    @Test
    public void subscribe_will_start_getting_events_test() throws Exception {

        try(EndpointWire wire = wire("subscriber-of-a")){
            AtomicInteger cnt = new AtomicInteger();
            wire.subscribeToEndpoint("a-publisher", Msg.Tick.class);
            wire.registerHandler(Msg.Tick.class, bus -> message -> cnt.incrementAndGet());
            wire.configure();

            send("a-publisher.events", Msg.fromInstance(new Msg.Tick()));

            Thread.sleep(4000);

            Assert.assertEquals(cnt.get(), 1);
        }
    }

    @Test
    public void send_submit_a_command_to_endpoint_input() throws Exception {
        try(EndpointWire wire = wire("main-controller")){
            wire.registerEndpointRoute("vehicle-b", Msg.TurnOff.class);
            wire.configure();

            MessageBus bus = wire.getMessageBus();
            bus.send(new Msg.TurnOff());

            Assert.assertEquals(countMessages("vehicle-b"), 1);
        }
    }

    @Test
    public void replay_will_will_send_a_message_back_to_sender() throws Exception {

        try(EndpointWire wire = wire("auto-raply")){
            wire.registerHandler(Msg.Ping.class, bus -> message -> bus.reply(new Msg.Pong()));
            wire.configure();

            send("auto-raply", Msg.fromInstance(new Msg.Ping(), "originaltor-ctrl"));

            Assert.assertEquals(countMessages("originaltor-ctrl"), 1);
        }
    }
}
