package com.pipeline.kafka.producers;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static com.pipeline.kafka.utils.ConfigConstants.BOOTSTRAP_SERVER;
import static com.pipeline.kafka.utils.ConfigConstants.DEFAULT_TOPIC;

public class DummyKafkaProducer {

    private static final int numberOfRecords = 25;
    private static final int numberOfTopics = 5;
    private static final Logger log = LoggerFactory.getLogger(DummyKafkaProducer.class);

    public static void main(String[] args) {
        log.info("Hello, world!");
        log.info("I am a Kafka Producer!");

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomPartitioner.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        for (int i = 0; i < numberOfRecords; i++) {

            String recordValue = Integer.toString(numberOfTopics * i);
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(DEFAULT_TOPIC, recordValue);

            producer.send(producerRecord, (recordMetadata, e) -> {
                if (e == null) {
                    log.info("Received new metadata: \n" +
                            "Topic: " + recordMetadata.topic() + "\n" +
                            "Partition: " + recordMetadata.partition() + "\n" +
                            "Offset: " + recordMetadata.offset() + "\n" +
                            "Timestamp: " + recordMetadata.timestamp());
                } else {
                    log.error("Error while producing: ", e);
                }
            });

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        producer.flush();

        producer.close();
    }
}
