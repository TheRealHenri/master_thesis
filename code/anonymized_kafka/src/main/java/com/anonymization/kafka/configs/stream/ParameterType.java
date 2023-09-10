package com.anonymization.kafka.configs.stream;

public enum ParameterType {
    KEYS("keys"),
    MAP("map"),
    BUCKET_SIZE("bucketSize"),
    N_FIELDS("nFields"),
    WINDOW_SIZE("windowSize"),
    GROUP_SIZE("groupSize"),
    NOISE("noise"),
    K("k"),
    L("l"),
    T("t");

    private final String name;

    ParameterType(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
