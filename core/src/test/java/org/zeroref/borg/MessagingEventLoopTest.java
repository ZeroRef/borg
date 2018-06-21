package org.zeroref.borg;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.zeroref.borg.lab.Env;
import org.zeroref.borg.lab.Msg;
import org.zeroref.borg.pipeline.DispatchMessagesToHandlers;
import org.zeroref.borg.recoverability.MessagingEventLoop;
import org.zeroref.borg.timeouts.Timeout;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MessagingEventLoopTest extends Env {

    @Test
    public void dispatch_integration() throws Exception {
        String kfk = CLUSTER.getKafkaConnect();
        List<String> inputTopics = Arrays.asList("dispatch-loop");

        AtomicInteger cnt = new AtomicInteger();

        DispatchMessagesToHandlers router = new DispatchMessagesToHandlers() {
            @Override
            public void dispatch(MessageEnvelope message) {
                cnt.getAndIncrement();
            }

            @Override
            public void dispatch(Timeout timeout) {
            }
        };

        try(MessagingEventLoop loop = new MessagingEventLoop("dispatch-loop", kfk, inputTopics, router)){
            loop.start();

            send("dispatch-loop", Msg.fromInstance(new Msg.Tick()));

            Thread.sleep(3000);

            Assert.assertEquals(cnt.get(), 1);
        }
    }
}
