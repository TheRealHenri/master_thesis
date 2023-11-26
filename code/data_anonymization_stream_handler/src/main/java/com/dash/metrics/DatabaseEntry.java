package com.dash.metrics;

public class DatabaseEntry {
    private String applicationId;
    private long timestamp;
    private double recordSendRate;
    private double recordsPerRequestAvg;
    private double processLatencyAvg;
    private double processRate;

    public DatabaseEntry() {
    }

    public String getApplicationId() {
        return applicationId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getRecordSendRate() {
        return recordSendRate;
    }

    public double getRecordsPerRequestAvg() {
        return recordsPerRequestAvg;
    }

    public double getProcessLatencyAvg() {
        return processLatencyAvg;
    }

    public double getProcessRate() {
        return processRate;
    }

    public void setApplicationId(String streamId) {
        this.applicationId = streamId;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setRecordSendRate(double recordSendRate) {
        this.recordSendRate = recordSendRate;
    }

    public void setRecordsPerRequestAvg(double recordsPerRequestAvg) {
        this.recordsPerRequestAvg = recordsPerRequestAvg;
    }

    public void setProcessLatencyAvg(double processLatencyAvg) {
        this.processLatencyAvg = processLatencyAvg;
    }

    public void setProcessRate(double processRate) {
        this.processRate = processRate;
    }
}
