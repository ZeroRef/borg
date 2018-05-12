package org.zeroref.borg.transport;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.zeroref.borg.MessageEnvelope;

import java.util.HashMap;
import java.util.Map;

public class MessageEnvelopeSerializer {
    private Gson gson = new Gson();

    public ProducerRecord<String, TransportRecord> envelopeToRecord(MessageEnvelope envelope, String topic) {
        HashMap<String, Object> content = new HashMap<>();
        content.put("type", envelope.getLocalMessage().getClass().getName());
        content.put("payload",  gson.toJson(envelope.getLocalMessage()));
        content.put("returnAddress", envelope.getReturnAddress());

        TransportRecord transportRecord = new TransportRecord(
                envelope.getUuid(),
                content,
                envelope.getHeaders()
        );

        return new ProducerRecord<>(topic, transportRecord);
    }

    public MessageEnvelope recordToEnvelope(TransportRecord record1) throws ClassNotFoundException {
        Map<String, Object> content = record1.getContent();

        Object payloadObj = content.get("payload");
        JsonObject jsonObject = gson.toJsonTree(payloadObj).getAsJsonObject();
        String payload = jsonObject.toString();

        String returnAddress = (String) content.get("returnAddress");
        Class type = Class.forName((String)content.get("type"));

        Object localMessage = gson.fromJson(payload, type);

        return new MessageEnvelope(
                record1.getUuid(),
                returnAddress,
                record1.getHeaders(),
                localMessage);
    }
}
