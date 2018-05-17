package org.zeroref.borg.transport;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroref.borg.MessageEnvelope;
import java.util.UUID;

public class MessageEnvelopeSerializer {
    private Gson gson = new Gson();
    private JsonParser parser = new JsonParser();

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageEnvelopeSerializer.class);

    public ProducerRecord<String, String> envelopeToRecord(MessageEnvelope envelope, String topic) {

        JsonObject record = new JsonObject();
        record.addProperty("id", envelope.getUuid().toString());
        record.addProperty("type", envelope.getLocalMessage().getClass().getName());
        record.addProperty("returnAddress", envelope.getReturnAddress());
        record.add("payload",  gson.toJsonTree(envelope.getLocalMessage()));

        return new ProducerRecord<>(topic, record.toString());
    }

    public MessageEnvelope recordToEnvelope(String record1) throws ClassNotFoundException {
        JsonObject envJson = (JsonObject)parser.parse(record1);

        UUID id = UUID.fromString(envJson.get("id").getAsString());
        String returnAddr = envJson.get("returnAddress").getAsString();
        Class type = Class.forName(envJson.get("type").getAsString());

        JsonObject payloadObj = (JsonObject)parser.parse(envJson.get("payload").toString());
        Object localMessage = gson.fromJson(payloadObj, type);

        return new MessageEnvelope(id, returnAddr, localMessage);
    }
}
