package com.dash.anonymizers.tablebased.datastructures;

public class DataPointClusterPair {

    private final DataPoint dataPoint;
    private final Cluster cluster;

    public DataPointClusterPair(DataPoint dataPoint, Cluster cluster) {
        this.dataPoint = dataPoint;
        this.cluster = cluster;
    }

    public DataPoint getDataPoint() {
        return dataPoint;
    }

    public Cluster getCluster() {
        return cluster;
    }
}
