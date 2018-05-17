package org.zeroref.borg.transport;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroref.borg.MessageEnvelope;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaMessageSender  {

    private KafkaProducer<String, String> producer = null;
    private final Properties props;
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMessageSender.class);
    private final AtomicBoolean running = new AtomicBoolean(true);

    private MessageEnvelopeSerializer serializer = new MessageEnvelopeSerializer();

    public KafkaMessageSender(String broker) {
        props = new Properties();
        props.put("bootstrap.servers", broker);
        props.put("client.id", "DemoProducer");
        props.put("auto.commit.interval.ms", 100);
        props.put("linger.ms", 50);
        props.put("block.on.buffer.full", true);
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());
    }

    public void start() {
        producer = new KafkaProducer<>(props);
        LOGGER.debug("Initialized sender");
    }

    public void stop() {
        producer.close();
        running.set(false);
    }

    public void send(List<String> dest, MessageEnvelope envelope) {
        try {
            for (String topic : dest){
                ProducerRecord<String, String> record = serializer.envelopeToRecord(envelope, topic);

                LOGGER.debug("Send to [{}] {}", topic, envelope.getLocalMessage().getClass().getSimpleName());

                if(!running.get())
                    return;

                producer.send(record).get();
                producer.flush();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
