package org.zeroref.borg.transport;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.zeroref.borg.lab.Env;
import org.zeroref.borg.MessageEnvelope;
import org.zeroref.borg.lab.Msg;

import java.util.Arrays;
import java.util.List;

public class KafkaMessageReceiverTest extends Env {

    @Test
    public void receive_message_from_topic() throws Exception {

        send("aaaaaaaa", Msg.from(Msg.Tick.class));

        List<String> subs = Arrays.asList("aaaaaaaa");

        KafkaMessageReceiver receiver = new KafkaMessageReceiver(CLUSTER.getKfkConnectionString(),subs);
        receiver.start();

        for (int i = 0; i < 5; i++) {
            try{
                MessageEnvelope messages = receiver.receive();
                Assert.assertNotNull(messages);

                receiver.commit(messages);

                break;
            }catch (AssertionError e){
                Thread.sleep(2000);
            }
        }
    }
}
