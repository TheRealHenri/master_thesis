package com.pipeline.kafka.utils;

public final class ConfigConstants {

    public static final String BOOTSTRAP_SERVER = "localhost:9092";
    public static final String DEFAULT_TOPIC = "no-filter";

    private ConfigConstants() {
        throw new AssertionError("This class should not be instantiated.");
    }
}
