package com.pipeline.kafka.utils;

public final class ConfigConstants {

    public static final String BOOTSTRAP_SERVER = "kafka:9092";
    public static final String DEFAULT_TOPIC = "no-filter";
    public static final String PATH_TO_CSV = "/tmp/syntheticData.csv";
    public static final int DEFAULT_BATCH_SIZE = 5;

    private ConfigConstants() {
        throw new AssertionError("This class should not be instantiated.");
    }
}
