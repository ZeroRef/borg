package org.zeroref.borg.transport;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroref.borg.MessageEnvelope;
import org.zeroref.borg.sagas.SagaPersistence;

import java.util.HashMap;
import java.util.Map;

public class MessageEnvelopeSerializer {
    private Gson gson = new Gson();
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageEnvelopeSerializer.class);

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

        String payload = "{}";

        try{
            if(!payload.equals(payloadObj)){
                JsonElement jsonElement = gson.toJsonTree(payloadObj);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                payload = jsonObject.toString();
            }
        }catch (Exception ex){
            LOGGER.debug("Failed to parse " + payloadObj);
        }

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
