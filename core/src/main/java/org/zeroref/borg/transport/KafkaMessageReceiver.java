package org.zeroref.borg.transport;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroref.borg.MessageEnvelope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class KafkaMessageReceiver {
    private  KafkaConsumer<String, String> consumer;
    private final Properties props;
    private final List<String> topics;

    private MessageEnvelopeSerializer serializer = new MessageEnvelopeSerializer();

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMessageReceiver.class);

    public KafkaMessageReceiver(String broker, String name, List<String> topics) {
        this.topics = topics;
        props = new Properties();
        props.put("bootstrap.servers", broker);
        props.put("group.id", "gr-"+name);
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("auto.offset.reset", "earliest");
        props.put("max.poll.records", "1"); /* records to include in 1 poll */
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
    }

    public void start() {
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(topics);

        LOGGER.debug("Initialized receiver");
    }

    public void stop() {
        consumer.wakeup();
        consumer.close();
    }

    public MessageEnvelope receive() throws Exception {

        ConsumerRecords<String, String> records = consumer.poll(500);

        for (ConsumerRecord<String, String> record : records) {
            MessageEnvelope envelope = serializer.recordToEnvelope(record.value());
            envelope.setOffset(recordOffset(record));

            return envelope;
        }

        return null;
    }

    HashMap<TopicPartition, OffsetAndMetadata> recordOffset(ConsumerRecord<String, String> record) {
        HashMap<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
        offsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset()+1));
        return offsets;
    }

    public void commit(MessageEnvelope message){
        consumer.commitSync((Map<TopicPartition, OffsetAndMetadata>) message.getOffset());
    }
}
