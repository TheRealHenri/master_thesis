package com.dash.metrics;

public class DatabaseEntry {
    private String applicationId;
    private long timestamp;
    private double recordSendRate;
    private double recordsPerRequestAvg;
    private double processLatencyAvg;
    private double processRate;
    private double messagesInPerSecond;
    private double bytesInPerSecond;
    private double bytesOutPerSecond;
    private double fetchRequestRate;
    private double produceRequestRate;
    private double requestLatencyAvg;
    private double requestLatencyMax;
    private double commitLatencyAvg;
    private double commitLatencyMax;
    private double requestSizeAvg;
    private double requestSizeMax;
    private double responseQueueTimeAvg;
    private double responseQueueTimeMax;
    private double responseSendTimeAvg;
    private double responseSendTimeMax;



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

    public double getMessagesInPerSecond() {
        return messagesInPerSecond;
    }

    public double getBytesInPerSecond() {
        return bytesInPerSecond;
    }

    public double getBytesOutPerSecond() {
        return bytesOutPerSecond;
    }

    public double getFetchRequestRate() {
        return fetchRequestRate;
    }

    public double getProduceRequestRate() {
        return produceRequestRate;
    }

    public double getRequestLatencyAvg() {
        return requestLatencyAvg;
    }

    public double getRequestLatencyMax() {
        return requestLatencyMax;
    }

    public double getCommitLatencyAvg() {
        return commitLatencyAvg;
    }

    public double getCommitLatencyMax() {
        return commitLatencyMax;
    }

    public double getRequestSizeAvg() {
        return requestSizeAvg;
    }

    public double getRequestSizeMax() {
        return requestSizeMax;
    }

    public double getResponseQueueTimeAvg() {
        return responseQueueTimeAvg;
    }

    public double getResponseQueueTimeMax() {
        return responseQueueTimeMax;
    }

    public double getResponseSendTimeAvg() {
        return responseSendTimeAvg;
    }

    public double getResponseSendTimeMax() {
        return responseSendTimeMax;
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

    public void setMessagesInPerSecond(double metricValue) {
        this.messagesInPerSecond = metricValue;
    }

    public void setBytesInPerSecond(double metricValue) {
        this.bytesInPerSecond = metricValue;
    }

    public void setBytesOutPerSecond(double metricValue) {
        this.bytesOutPerSecond = metricValue;
    }


    public void setFetchRequestRate(double metricValue) {
        this.fetchRequestRate = metricValue;
    }

    public void setProduceRequestRate(double metricValue) {
        this.produceRequestRate = metricValue;
    }

    public void setRequestLatencyAvg(double metricValue) {
        this.requestLatencyAvg = metricValue;
    }

    public void setRequestLatencyMax(double metricValue) {
        this.requestLatencyMax = metricValue;
    }

    public void setCommitLatencyAvg(double metricValue) {
        this.commitLatencyAvg = metricValue;
    }

    public void setCommitLatencyMax(double metricValue) {
        this.commitLatencyMax = metricValue;
    }

    public void setRequestSizeAvg(double metricValue) {
        this.requestSizeAvg = metricValue;
    }

    public void setRequestSizeMax(double metricValue) {
        this.requestSizeMax = metricValue;
    }

    public void setResponseQueueTimeAvg(double metricValue) {
        this.responseQueueTimeAvg = metricValue;
    }

    public void setResponseQueueTimeMax(double metricValue) {
        this.responseQueueTimeMax = metricValue;
    }

    public void setResponseSendTimeAvg(double metricValue) {
        this.responseSendTimeAvg = metricValue;
    }

    public void setResponseSendTimeMax(double metricValue) {
        this.responseSendTimeMax = metricValue;
    }
}
