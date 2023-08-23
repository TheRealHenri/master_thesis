package com.anonymization.kafka.streams;

import com.anonymization.kafka.AnonymizationCategory;
import com.anonymization.kafka.configs.AnonymizationStreamConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnonymizationStream {

    private AnonymizationCategory category;
    private String topic;
    private AnonymizationStreamConfig config;
    private volatile StreamState state;
    private static final Logger log = LoggerFactory.getLogger(AnonymizationStream.class);

    public AnonymizationStream(AnonymizationCategory category, String topic, AnonymizationStreamConfig config) {
        this.category = category;
        this.topic = topic;
        this.config = config;
        this.state = StreamState.INITIALIZED;
    }

    public void start() {
        state = StreamState.STARTED;
    }

    public void pause() {
        if (state == StreamState.STARTED) {
            state = StreamState.PAUSED;
        } else {
            log.warn("Stream {} is not started", config.getApplicationId());
        }
    }

    public void stop() {
        if (state == StreamState.STARTED || state == StreamState.PAUSED) {
            state = StreamState.STOPPED;
        } else {
            log.warn("Stream {} is not started or paused", config.getApplicationId());
        }
    }

    public AnonymizationCategory getCategory() {
        return category;
    }

    public String getTopic() {
        return topic;
    }

    public AnonymizationStreamConfig getConfig() {
        return config;
    }

    public StreamState getState() {
        return state;
    }
}
