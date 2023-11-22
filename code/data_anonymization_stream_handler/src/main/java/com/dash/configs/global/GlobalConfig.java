package com.dash.configs.global;

import com.dash.configs.global.schemas.DataSchema;

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
}
