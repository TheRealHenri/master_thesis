package com.anonymization.kafka.anonymizers.tablebased.datastructures;

import java.util.LinkedList;
import java.util.Queue;

public class RollingAverage {
    private final int mu;
    private final Queue<Double> lastValues;
    private double sum;

    public RollingAverage(int mu) {
        this.mu = mu;
        this.lastValues = new LinkedList<>();
        this.sum = 0;
    }

    public void addValue(double value) {
        sum += value;
        lastValues.add(value);
        if (lastValues.size() > mu) {
            sum -= lastValues.poll();
        }
    }

    public double get() {
        if (lastValues.isEmpty()) {
            return 0;
        }
        return sum / lastValues.size();
    }
}
