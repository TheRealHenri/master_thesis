package com.anonymization.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class CommandConsumer {

    private String bootstrap_server = "";
    private final String topic = "anonymized_kafka_commands";
    private final String groupId = "command_consumer";
    private volatile boolean running = true;
    private KafkaConsumer<String, String> consumer;
    private static final Logger log = LoggerFactory.getLogger(CommandConsumer.class);


    public void createConsumer(String bootstrapServer) {
        this.bootstrap_server = bootstrapServer;
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_server);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumer = new KafkaConsumer<>(properties);
    }

    public void startConsumer() {
        consumer.subscribe(List.of(topic));
        while(running) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {

                String recordValue = record.value();
                log.info("Received command: {}", recordValue);
                String[] args = recordValue.split(" ");
                EntryPoint.main(args);
            }
        }
    }

    public void stopConsumer() {
        running = false;
        consumer.close();
    }
}
