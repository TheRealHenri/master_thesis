package com.anonymization.kafka.configs.stream;

public class QuasiIdentifier {

    String key;
    GeneralizationHierarchy hierarchy;


    public QuasiIdentifier(String key, GeneralizationHierarchy hierarchy) {
        this.key = key;
        this.hierarchy = hierarchy;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public GeneralizationHierarchy getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(GeneralizationHierarchy hierarchy) {
        this.hierarchy = hierarchy;
    }
}
