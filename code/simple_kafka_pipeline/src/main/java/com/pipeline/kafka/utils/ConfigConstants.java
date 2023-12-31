package com.pipeline.kafka.utils;

public final class ConfigConstants {

    public static final String BOOTSTRAP_SERVER = "kafka1:19092,kafka2:19093,kafka3:19094";
    public static final String DEFAULT_TOPIC = "no-filter";
    public static final String DEFAULT_KEY = "default_key";
    public static final String PATH_TO_CSV = "/tmp/data-generator-datasets/single/syntheticData.csv";
    //public static final String PATH_TO_CSV = "/Users/allgower/Uni/TUB/MA/master_thesis/code/data_generator/datasets/single/syntheticData.csv";
    public static final int DEFAULT_BATCH_SIZE = 5;
    public static final int LINGER_MS = 100;

    private ConfigConstants() {
        throw new AssertionError("This class should not be instantiated.");
    }
}
