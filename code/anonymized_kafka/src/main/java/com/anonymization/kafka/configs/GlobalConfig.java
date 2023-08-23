package com.anonymization.kafka.configs;

import org.apache.kafka.common.protocol.types.Schema;

public class GlobalConfig {
    private final String bootstrapServer;
    private final String topic;
    private final Schema dataSchema;

    public GlobalConfig(String bootstrapServer, String topic, Schema dataSchema) {
        this.bootstrapServer = bootstrapServer;
        this.topic = topic;
        this.dataSchema = dataSchema;
    }

    public String getBootstrapServer() {
        return bootstrapServer;
    }

    public String getTopic() {
        return topic;
    }

    public Schema getDataSchema() {
        return dataSchema;
    }
}
