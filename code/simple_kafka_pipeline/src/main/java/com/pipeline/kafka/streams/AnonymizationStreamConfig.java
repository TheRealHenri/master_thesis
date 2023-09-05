package com.pipeline.kafka.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;

public class AnonymizationStreamConfig {

    public final String applicationId;
    public final Anonymizer anonymizer;

    public AnonymizationStreamConfig(String applicationId, Anonymizer anonymizer) {
        this.applicationId = applicationId;
        this.anonymizer = anonymizer;
    }

    public Properties getStreamProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return props;
    }
}
