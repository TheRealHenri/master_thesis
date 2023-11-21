package com.anonymization.kafka.anonymizers.tablebased.datastructures;

public class NumericalGeneralization implements Generalization {

    private final int bucketSize;
    private final int minimumValue;
    private final int maximumValue;
    private int currentStartRange;
    private int currentEndRange;

    public NumericalGeneralization(int bucketSize, int minimumValue, int maximumValue) {
        this.bucketSize = bucketSize;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.currentStartRange = maximumValue;
        this.currentEndRange = minimumValue;
    }

    @Override
    public String getGeneralization() {
        return "[" + currentStartRange + ", " + currentEndRange + "]";
    }

    @Override
    public boolean fitsGeneralization(String value) {
        int intValue = Integer.parseInt(value);
        return intValue >= currentStartRange && intValue <= currentEndRange;
    }

    @Override
    public double calculateEnlargementCost(String value) {
        return getInformationLossIncluding(value) - getCurrentInformationLoss();
    }

    @Override
    public double calculateEnlargementCost(Generalization generalization) {
        if (!(generalization instanceof NumericalGeneralization)) {
            throw new IllegalArgumentException("Generalization must be of type NumericalGeneralization");
        }
        NumericalGeneralization numericalGeneralization = (NumericalGeneralization) generalization;
        int potentialNewStart = Math.min(currentStartRange, numericalGeneralization.currentStartRange);
        int potentialNewEnd = Math.max(currentEndRange, numericalGeneralization.currentEndRange);
        double potentialNewCost = getInformationLossIncluding(String.valueOf(potentialNewStart)) + getInformationLossIncluding(String.valueOf(potentialNewEnd));
        return potentialNewCost - getCurrentInformationLoss();
    }

    @Override
    public double getInformationLossIncluding(String value) {
        int intValue = Integer.parseInt(value);

        int newStartRange = Math.min(currentStartRange, intValue);
        int newEndRange = Math.max(currentEndRange, intValue);

        newStartRange = (newStartRange / bucketSize) * bucketSize;
        newEndRange = ((newEndRange + bucketSize - 1) / bucketSize) * bucketSize;

        return getInformationCostFor(newStartRange, newEndRange);
    }

    @Override
    public double getCurrentInformationLoss() {
        return getInformationCostFor(currentStartRange, currentEndRange);
    }

    @Override
    public void generalize(String value) {
        int intValue = Integer.parseInt(value);

        if (fitsGeneralization(value)) {
            return;
        }

        int newStartRange = Math.min(currentStartRange, intValue);
        int newEndRange = Math.max(currentEndRange, intValue);

        currentStartRange = (newStartRange / bucketSize) * bucketSize;
        currentEndRange = ((newEndRange + bucketSize - 1) / bucketSize) * bucketSize;
    }

    @Override
    public void mergeGeneralization(Generalization generalization) {
        if (!(generalization instanceof NumericalGeneralization)) {
            throw new IllegalArgumentException("Cannot merge generalization of type " + generalization.getClass().getName());
        }

        NumericalGeneralization numericalGeneralization = (NumericalGeneralization) generalization;

        int newStartRange = Math.min(currentStartRange, numericalGeneralization.currentStartRange);
        int newEndRange = Math.max(currentEndRange, numericalGeneralization.currentEndRange);

        currentStartRange = (newStartRange / bucketSize) * bucketSize;
        currentEndRange = ((newEndRange + bucketSize - 1) / bucketSize) * bucketSize;
    }

    private double getInformationCostFor(int startRange, int endRange) {
        return ((double) (endRange - startRange)) / ((double) (maximumValue - minimumValue));
    }
}
