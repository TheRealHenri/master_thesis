package com.anonymization.kafka.loaders;

import com.anonymization.kafka.configs.SystemConfiguration;

import java.util.Map;

public class PropertiesLoader {
    private Map<String, String> props;

    public PropertiesLoader (Map<String, String> props) {
        this.props = props;
    }

    public SystemConfiguration parsePropertiesFile() {
        return null;
    }
}
