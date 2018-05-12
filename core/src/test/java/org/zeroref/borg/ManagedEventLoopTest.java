package org.zeroref.borg;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.zeroref.borg.lab.Env;
import org.zeroref.borg.lab.Msg;
import org.zeroref.borg.pipeline.DispatchMessagesToHandlers;
import org.zeroref.borg.recoverability.ManagedEventLoop;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ManagedEventLoopTest extends Env {

    @Test
    public void dispatch_integration() throws Exception {
        String kfk = CLUSTER.getKafkaConnect();
        List<String> inputTopics = Arrays.asList("dispatch-loop");

        AtomicInteger cnt = new AtomicInteger();
        DispatchMessagesToHandlers handlers = message -> {
            cnt.getAndIncrement();
        };

        try(ManagedEventLoop loop = new ManagedEventLoop("dispatch-loop", kfk, inputTopics, handlers)){
            loop.start();

            send("dispatch-loop", Msg.fromInstance(new Msg.Tick()));

            Thread.sleep(3000);

            Assert.assertEquals(cnt.get(), 1);
        }
    }
}
