package com.pipeline.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyKafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(DummyKafkaProducer.class);

    public static void main(String[] args) {
        log.info("Hello, world!");
        log.info("I am a Kafka Producer!");
    }
}
