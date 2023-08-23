package com.anonymization.kafka.configs.global;

import com.anonymization.kafka.configs.global.schemas.DataSchema;

public class GlobalConfig {
    private String bootstrapServer;
    private String topic;
    private DataSchema dataSchema;

    public GlobalConfig() {}
    public GlobalConfig(String bootstrapServer, String topic, DataSchema dataSchema) {
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

    public DataSchema getDataSchema() {
        return dataSchema;
    }

    public void setBootstrapServer(String bootstrapServer) {
        this.bootstrapServer = bootstrapServer;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setDataSchema(DataSchema dataSchema) {
        this.dataSchema = dataSchema;
    }
}
