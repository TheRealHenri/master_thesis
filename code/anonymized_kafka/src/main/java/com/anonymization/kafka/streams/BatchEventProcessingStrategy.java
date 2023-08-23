package com.anonymization.kafka.streams;

public class BatchEventProcessingStrategy implements StreamProcessingStrategy {
    private int batchSize;

    public BatchEventProcessingStrategy(int batchSize) {
        this.batchSize = batchSize;
    }


    @Override
    public void process() {
        // do stuff
    }
}
