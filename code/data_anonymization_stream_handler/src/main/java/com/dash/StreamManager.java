package com.dash;

import com.dash.builders.AnonymizationStreamConfigBuilder;
import com.dash.configs.AnonymizationStreamConfig;
import com.dash.configs.SystemConfiguration;
import com.dash.configs.global.GlobalConfig;
import com.dash.configs.global.schemas.SchemaCommon;
import com.dash.configs.stream.StreamProperties;
import com.dash.factory.AnonymizationStreamFactory;
import com.dash.loaders.JSONLoader;
import com.dash.metrics.DatabaseEntry;
import com.dash.metrics.DatabaseManager;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.streams.KafkaStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StreamManager {

    private final HashMap<String, KafkaStreams> streamsMap = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(StreamManager.class);
    private CommandConsumer commandConsumer = null;

    private static final class ManagerInstanceHolder {
        private static final StreamManager instance = new StreamManager();
    }

    public static StreamManager getInstance() {
        return ManagerInstanceHolder.instance;
    }

    public void initializeStreams() {
        log.info("Initializing streams");
        if (!streamsMap.isEmpty()) {
            log.info("Streams already initialized. Stopping all streams.");
            stopAllStreams();
            streamsMap.clear();
        }
        SystemConfiguration systemConfiguration = JSONLoader.loadConfig();
        if (commandConsumer == null) {
            try {
                log.info("Starting command consumer");
                commandConsumer = new CommandConsumer();
                commandConsumer.createConsumer(systemConfiguration.getGlobalConfig().getBootstrapServer());
                new Thread(() -> commandConsumer.startConsumer()).start();
            } catch (Exception e) {
                log.error("Error while starting command consumer");
                log.error(e.getMessage());
            }
        }
        SchemaCommon schemaCommon = systemConfiguration.getGlobalConfig().getDataSchema().getSchema();
        log.info("Starting to build StreamConfigs");
        AnonymizationStreamConfigBuilder configBuilder = new AnonymizationStreamConfigBuilder(schemaCommon);
        List<StreamProperties> streamProperties = systemConfiguration.getStreamProperties();
        ArrayList<AnonymizationStreamConfig> streamConfigs = new ArrayList<>();
        for (StreamProperties streamProperty : streamProperties) {
            try {
                streamConfigs.add(configBuilder.build(streamProperty));
            } catch (Exception e) {
                log.error("Error while building stream config for stream {}", streamProperty.getApplicationId());
                log.error(e.getMessage());
            }
        }
        log.info("Creating {} streams from configs", streamConfigs.size());
        GlobalConfig globalConfig = systemConfiguration.getGlobalConfig();
        for (AnonymizationStreamConfig streamConfig : streamConfigs) {
            streamsMap.put(streamConfig.getApplicationId(), AnonymizationStreamFactory.buildAnonymizationStream(globalConfig, streamConfig));
        }
        log.info("Streams created.");
        log.info("Setting up metrics database");
        String dbURLAppendix = System.currentTimeMillis() + ".db";
        DatabaseManager databaseManager = new DatabaseManager(dbURLAppendix);
        log.info("Starting all streams");
        startAllStreamsWithMetrics(databaseManager);
    }


    public void startAllStreamsWithMetrics(DatabaseManager databaseManager) {
        for (KafkaStreams stream : streamsMap.values()) {
            if (stream.isPaused()) {
                stream.resume();
            } else {
                stream.start();
            }
        }
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            List<DatabaseEntry> batch = new ArrayList<>();
            for (Map.Entry<String, KafkaStreams> kafkaStreamsEntry : streamsMap.entrySet()) {
                Map<MetricName, ? extends Metric> metrics = kafkaStreamsEntry.getValue().metrics();
                DatabaseEntry databaseEntry = createDatabaseEntry(kafkaStreamsEntry.getKey(), metrics);
                batch.add(databaseEntry);
            }
            databaseManager.executeBatchInserts(batch);
        }, 0, 1, TimeUnit.SECONDS);
    }

    private DatabaseEntry createDatabaseEntry(String applicationId, Map<MetricName, ? extends Metric> metrics) {
        DatabaseEntry databaseEntry = new DatabaseEntry();
        databaseEntry.setApplicationId(applicationId);
        databaseEntry.setTimestamp(System.currentTimeMillis());
        databaseEntry.setRecordSendRate(getMetricValue(metrics, "record-send-rate"));
        databaseEntry.setRecordsPerRequestAvg(getMetricValue(metrics, "records-per-request-avg"));
        databaseEntry.setProcessLatencyAvg(getMetricValue(metrics, "process-latency-avg"));
        databaseEntry.setProcessRate(getMetricValue(metrics, "process-rate"));
        databaseEntry.setMessagesInPerSecond(getMetricValue(metrics, "messages-in-per-second"));
        databaseEntry.setFetchRequestRate(getMetricValue(metrics, "fetch-request-rate"));
        databaseEntry.setProduceRequestRate(getMetricValue(metrics, "produce-request-rate"));
        databaseEntry.setRequestLatencyAvg(getMetricValue(metrics, "request-latency-avg"));
        databaseEntry.setRequestLatencyMax(getMetricValue(metrics, "request-latency-max"));
        databaseEntry.setCommitLatencyAvg(getMetricValue(metrics, "commit-latency-avg"));
        databaseEntry.setCommitLatencyMax(getMetricValue(metrics, "commit-latency-max"));
        databaseEntry.setRequestSizeAvg(getMetricValue(metrics, "request-size-avg"));
        databaseEntry.setRequestSizeMax(getMetricValue(metrics, "request-size-max"));
        databaseEntry.setResponseQueueTimeAvg(getMetricValue(metrics, "response-queue-time-avg"));
        databaseEntry.setResponseQueueTimeMax(getMetricValue(metrics, "response-queue-time-max"));
        databaseEntry.setResponseSendTimeAvg(getMetricValue(metrics, "response-send-time-avg"));
        databaseEntry.setResponseSendTimeMax(getMetricValue(metrics, "response-send-time-max"));
        return databaseEntry;
    }

    private double getMetricValue(Map<MetricName, ? extends Metric> metrics, String metricName) {
        return metrics.entrySet().stream()
                .filter(entry -> entry.getKey().name().equals(metricName))
                .findFirst()
                .map(entry -> {
                    Double value = (Double) entry.getValue().metricValue();
                    return value.isNaN() ? 0.0 : value;
                })
                .orElse(0.0);
    }

    public void startAllStreams() {
        for (KafkaStreams stream : streamsMap.values()) {
            if (stream.isPaused()) {
                stream.resume();
            } else {
                stream.start();
            }
        }
    }
    public void stopAllStreams() {
        for (KafkaStreams stream : streamsMap.values()) {
            stream.close();
        }
    }

    public void pauseAllStreams() {
        for (KafkaStreams stream : streamsMap.values()) {
            stream.pause();
        }
    }

    public void startStream(String applicationId) {
        if (streamsMap.containsKey(applicationId)) {
            streamsMap.get(applicationId).start();
        } else {
            log.error("Stream {} does not exist", applicationId);
        }
    }

    public void stopStream(String applicationId) {
        if (streamsMap.containsKey(applicationId)) {
            streamsMap.get(applicationId).close();
        } else {
            log.error("Stream {} does not exist", applicationId);
        }
    }

    public void pauseStream(String applicationId) {
        if (streamsMap.containsKey(applicationId)) {
            streamsMap.get(applicationId).pause();
        } else {
            log.error("Stream {} does not exist", applicationId);
        }
    }

    public void listStreams() {
        log.info("Listing all streams:");
        if (streamsMap.isEmpty()) {
            log.info("Currently no streams running");
        }
        for (Map.Entry<String, KafkaStreams> stream : streamsMap.entrySet()) {
            log.info("Stream " + stream.getKey() + " is in state " + stream.getValue().state());
        }
    }

    public void close() {
        log.info("Closing system");
        log.info("Stopping all streams");
        stopAllStreams();
        log.info("Stopping command consumer");
        commandConsumer.stopConsumer();
        commandConsumer = null;
        log.info("System closed");
    }
}
