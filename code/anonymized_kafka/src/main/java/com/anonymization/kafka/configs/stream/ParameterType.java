package com.anonymization.kafka.configs.stream;

public enum ParameterType {
    KEYS("keys"),
    GENERALIZATION_MAP("generalizationMap"),
    CONDITION_MAP("conditionMap"),
    BUCKET_SIZE("bucketSize"),
    N_FIELDS("nFields"),
    WINDOW_SIZE("windowSize"),
    ADVANCE_TIME("advanceTime"),
    GRACE_PERIOD("gracePeriod"),
    GROUP_SIZE("groupSize"),
    NOISE("noise"),
    SUBSTITUTION_LIST("substitutionList"),
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
