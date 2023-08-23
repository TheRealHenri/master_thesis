package com.anonymization.kafka.configs;

import java.util.Set;

public class SystemConfiguration {
    private final GlobalConfig globalConfig;
    private final Set<StreamProperties> streamProperties;

    public SystemConfiguration(GlobalConfig globalConfig, Set<StreamProperties> streamProperties) {
        this.globalConfig = globalConfig;
        this.streamProperties = streamProperties;
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public Set<StreamProperties> getStreamProperties() {
        return streamProperties;
    }
}
