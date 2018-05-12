package org.zeroref.borg.sagas;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.zeroref.borg.lab.Env;
import org.zeroref.borg.lab.Msg;
import org.zeroref.borg.runtime.EndpointWire;
import org.zeroref.borg.sagas.domain.*;

public class SagaWiredTest extends Env {

    @Test
    public void handle_saga_event() throws Exception {

        try(EndpointWire wire = wire("saga-subscriber-of-evt")){
            wire.subscribeToEndpoint("b-publisher", Booom.class);
            wire.registerSagas(MiniSaga.class);
            wire.configure();

            send("b-publisher.events", Msg.fromInstance(new Booom("o1")));

            Thread.sleep(4000);

            Assert.assertEquals(MiniSagaState.called, true);
        }
    }
}
