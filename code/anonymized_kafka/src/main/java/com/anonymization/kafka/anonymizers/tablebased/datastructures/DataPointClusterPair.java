package com.anonymization.kafka.anonymizers.tablebased.datastructures;

import org.apache.kafka.connect.data.Struct;

public class DataPointClusterPair {

    private final Struct dataPoint;
    private final Cluster cluster;

    public DataPointClusterPair(Struct dataPoint, Cluster cluster) {
        this.dataPoint = dataPoint;
        this.cluster = cluster;
    }

    public Struct getDataPoint() {
        return dataPoint;
    }

    public Cluster getCluster() {
        return cluster;
    }
}
