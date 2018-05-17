package org.zeroref.borg.transport;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.zeroref.borg.MessageEnvelope;
import org.zeroref.borg.sagas.domain.Booom;
import java.util.UUID;

public class MessageEnvelopeSerializerTest {
    MessageEnvelopeSerializer serializer = new MessageEnvelopeSerializer();
    private JsonParser parser = new JsonParser();

    @Test
    public void encode_to_string(){
        UUID uuid = UUID.randomUUID();
        Booom message = new Booom("!");
        MessageEnvelope envelope = new MessageEnvelope(uuid, "retAddr", message);

        ProducerRecord<String, String> rc = serializer.envelopeToRecord(envelope, "destTopic");

        Assert.assertEquals(rc.topic(), "destTopic");

        JsonObject envJson = (JsonObject)parser.parse(rc.value());
        Assert.assertEquals(envJson.get("id").getAsString(), uuid.toString());
        Assert.assertEquals(envJson.get("type").getAsString(), "org.zeroref.borg.sagas.domain.Booom");
        Assert.assertEquals(envJson.get("returnAddress").getAsString(), "retAddr");
        JsonObject payloadObj = (JsonObject)parser.parse(envJson.get("payload").toString());
        Assert.assertEquals(payloadObj.get("id").getAsString(), "!");
    }

    @Test
    public void encode_from_string() throws ClassNotFoundException {
        String record = "{" +
                "\"id\":\"f8ce617c-6445-4141-9cd7-78fa0bf2210c\"," +
                "\"type\":\"org.zeroref.borg.sagas.domain.Booom\"," +
                "\"returnAddress\":\"retAddr\"," +
                "\"payload\":{\"id\":\"!\"}" +
                "}\n";

        MessageEnvelope envelope = serializer.recordToEnvelope(record);

        Assert.assertEquals(envelope.getUuid(), UUID.fromString("f8ce617c-6445-4141-9cd7-78fa0bf2210c"));
        Assert.assertEquals(envelope.getReturnAddress(), "retAddr");
        Assert.assertNotNull(envelope.getLocalMessage());
    }
}
