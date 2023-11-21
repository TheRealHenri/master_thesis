package com.anonymization.kafka.anonymizers.tablebased.datastructures;

import java.util.HashMap;
import java.util.HashSet;

public class CategoricalGeneralization implements Generalization {

    private final int totalNumberOfLeaves;
    private final HashMap<String, CategoricalHierarchy> leafMap;
    private CategoricalHierarchy currentNode;

    public CategoricalGeneralization(int totalNumberOfLeaves, HashMap<String, CategoricalHierarchy> leafMap, CategoricalHierarchy currentNode) {
        this.totalNumberOfLeaves = totalNumberOfLeaves;
        this.leafMap = leafMap;
        this.currentNode = currentNode;
    }

    @Override
    public String getGeneralization() {
        return currentNode.getValue();
    }

    @Override
    public boolean fitsGeneralization(String value) {
        return currentNode.fitsGeneralization(value);
    }

    @Override
    public double calculateEnlargementCost(String value) {
        if (fitsGeneralization(value)) {
            return 0;
        }

        return getInformationLossIncluding(value) - getCurrentInformationLoss();
    }

    @Override
    public double calculateEnlargementCost(Generalization generalization) {
        if (!(generalization instanceof CategoricalGeneralization)) {
            throw new IllegalArgumentException("Generalization must be of type CategoricalGeneralization");
        }
        CategoricalGeneralization categoricalGeneralization = (CategoricalGeneralization) generalization;
        CategoricalHierarchy commonAncestor = findClosestGeneralization(categoricalGeneralization.getCurrentNode());
        return calculateInformationLossFor(commonAncestor) - getCurrentInformationLoss();
    }

    @Override
    public double getInformationLossIncluding(String value) {
        if (fitsGeneralization(value)) {
            return getCurrentInformationLoss();
        }

        CategoricalHierarchy leafNode = leafMap.get(value);
        return calculateInformationLossFor(findClosestGeneralization(leafNode));
    }

    @Override
    public double getCurrentInformationLoss() {
        return calculateInformationLossFor(currentNode);
    }

    @Override
    public void generalize(String value) {
        if (fitsGeneralization(value)) {
            return;
        }

        currentNode = findClosestGeneralization(leafMap.get(value));
    }

    @Override
    public void mergeGeneralization(Generalization generalization) {
        if (!(generalization instanceof CategoricalGeneralization)) {
            throw new IllegalArgumentException("Generalization must be of type CategoricalGeneralization");
        }
        CategoricalGeneralization categoricalGeneralization = (CategoricalGeneralization) generalization;
        currentNode = findClosestGeneralization(categoricalGeneralization.getCurrentNode());
    }

    private double calculateInformationLossFor(CategoricalHierarchy node) {
        return ((double) node.getNumberOfLeafNodes() - 1) / (totalNumberOfLeaves - 1);
    }

    private CategoricalHierarchy findClosestGeneralization(CategoricalHierarchy value) {
        HashSet<CategoricalHierarchy> ancestors = new HashSet<>();

        while (value != null) {
            ancestors.add(value);
            value = value.getParent();
        }

        CategoricalHierarchy tmpNode = currentNode;

        while (tmpNode != null) {
            if (ancestors.contains(tmpNode)) {
                return tmpNode;
            }
            tmpNode = tmpNode.getParent();
        }

        return null;
    }

    public CategoricalHierarchy getCurrentNode() {
        return currentNode;
    }
}
