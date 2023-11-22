package com.dash.anonymizers.attributebased.datastructures;

import java.util.List;

public class Edge {

    private final int to;
    private final double weight;
    private double value;
    private final List<Integer> correspondingGroupIndices;

    public Edge(int to, double weight, double value, List<Integer> correspondingGroupIndices) {
        this.to = to;
        this.weight = weight;
        this.value = value;
        this.correspondingGroupIndices = correspondingGroupIndices;
    }

    public int getTo() {
        return to;
    }

    public double getWeight() {
        return weight;
    }

    public double getValue() {
        return value;
    }

    public List<Integer> getCorrespondingGroupIndices() {
        return correspondingGroupIndices;
    }
}
