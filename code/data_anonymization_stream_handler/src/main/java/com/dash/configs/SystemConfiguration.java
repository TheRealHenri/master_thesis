package com.dash.configs;

import com.dash.configs.global.GlobalConfig;
import com.dash.configs.stream.StreamProperties;

import java.util.List;

public class SystemConfiguration {
    private GlobalConfig globalConfig;
    private List<StreamProperties> streamProperties;

    public SystemConfiguration(){}
    public SystemConfiguration(GlobalConfig globalConfig, List<StreamProperties> streamProperties) {
        this.globalConfig = globalConfig;
        this.streamProperties = streamProperties;
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public List<StreamProperties> getStreamProperties() {
        return streamProperties;
    }

    public void setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    public void setStreamProperties(List<StreamProperties> streamProperties) {
        this.streamProperties = streamProperties;
    }
}
