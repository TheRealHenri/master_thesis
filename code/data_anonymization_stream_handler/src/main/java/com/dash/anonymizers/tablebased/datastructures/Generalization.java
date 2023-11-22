package com.dash.anonymizers.tablebased.datastructures;

public interface Generalization {
    String getGeneralization();
    boolean fitsGeneralization(String value);
    double calculateEnlargementCost(String value);
    double calculateEnlargementCost(Generalization generalization);
    double getInformationLossIncluding(String value);
    double getCurrentInformationLoss();
    void generalize(String value);
    void mergeGeneralization(Generalization generalization);
}
