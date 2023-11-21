package com.anonymization.kafka.anonymizers.tablebased.datastructures;

import java.util.List;

public class CategoricalHierarchy implements GeneralizationHierarchy {
    private final String value;
    private CategoricalHierarchy parent = null;
    private final List<CategoricalHierarchy> children;

    public CategoricalHierarchy(String value, List<CategoricalHierarchy> children) {
        this.value = value;
        this.children = children;
        for (CategoricalHierarchy child : children) {
            child.setParent(this);
        }
    }

    public int getNumberOfLeafNodes() {
        if (isLeafNode()) {
            return 1;
        }
        int numberOfLeafNodes = 0;
        for (CategoricalHierarchy child : children) {
            numberOfLeafNodes += child.getNumberOfLeafNodes();
        }
        return numberOfLeafNodes;
    }

    public boolean fitsGeneralization(String value) {
        if (this.value.equals(value)) {
            return true;
        }

        for (CategoricalHierarchy child : children) {
            return child.fitsGeneralization(value);
        }

        return false;
    }

    public String getValue() {
        return value;
    }

    public List<CategoricalHierarchy> getChildren() {
        return children;
    }

    public CategoricalHierarchy getParent() {
        return parent;
    }

    public void setParent(CategoricalHierarchy parent) {
        this.parent = parent;
    }

    public boolean isLeafNode() {
        return children.isEmpty();
    }
}
