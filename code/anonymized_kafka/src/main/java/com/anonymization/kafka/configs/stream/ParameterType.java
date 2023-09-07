package com.anonymization.kafka.configs.stream;

public enum ParameterType {
    KEYS("keys"),
    BUCKET_SIZE("bucketSize"),
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
