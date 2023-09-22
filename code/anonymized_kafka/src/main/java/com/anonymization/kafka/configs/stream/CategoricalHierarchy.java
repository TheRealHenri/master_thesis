package com.anonymization.kafka.configs.stream;

import java.util.List;

public class CategoricalHierarchy implements GeneralizationHierarchy {
    private String value;
    private List<CategoricalHierarchy> children;

    public CategoricalHierarchy(String value, List<CategoricalHierarchy> children) {
        this.value = value;
        this.children = children;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<CategoricalHierarchy> getChildren() {
        return children;
    }

    public void setChildren(List<CategoricalHierarchy> children) {
        this.children = children;
    }
}
