package com.dash.anonymizers.tablebased.datastructures;

public class NumericalHierarchy implements GeneralizationHierarchy {

    private int bucketSize;
    private int rangeStart;
    private int rangeEnd;

    public NumericalHierarchy(int bucketSize, int rangeStart, int rangeEnd) {
        this.bucketSize = bucketSize;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
    }

    public int getBucketSize() {
        return bucketSize;
    }

    public int getRangeStart() {
        return rangeStart;
    }

    public int getRangeEnd() {
        return rangeEnd;
    }

    public void setBucketSize(int bucketSize) {
        this.bucketSize = bucketSize;
    }

    public void setRangeStart(int rangeStart) {
        this.rangeStart = rangeStart;
    }

    public void setRangeEnd(int rangeEnd) {
        this.rangeEnd = rangeEnd;
    }
}
