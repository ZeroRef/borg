package org.zeroref.borg.transport;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.zeroref.borg.lab.Env;
import org.zeroref.borg.MessageEnvelope;
import org.zeroref.borg.lab.Msg;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class KafkaMessageSenderTest extends Env {
    @Test
    public void send_message_over_topic() {

        KafkaMessageSender sender = new KafkaMessageSender(CLUSTER.getKfkConnectionString());
        sender.start();

        MessageEnvelope envelope = new MessageEnvelope(
                UUID.randomUUID(),
                "b",
                new HashMap<>(),
                new Msg.Tick()

        );
        sender.send(Arrays.asList("a"), envelope);

        Assert.assertEquals(countMessages("a"), 1);
    }


}
