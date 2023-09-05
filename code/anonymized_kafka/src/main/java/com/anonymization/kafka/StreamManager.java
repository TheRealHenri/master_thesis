package com.anonymization.kafka;

import com.anonymization.kafka.builders.AnonymizationStreamConfigBuilder;
import com.anonymization.kafka.configs.AnonymizationStreamConfig;
import com.anonymization.kafka.configs.SystemConfiguration;
import com.anonymization.kafka.configs.global.GlobalConfig;
import com.anonymization.kafka.configs.global.schemas.SchemaCommon;
import com.anonymization.kafka.configs.stream.StreamProperties;
import com.anonymization.kafka.factory.AnonymizationStreamFactory;
import com.anonymization.kafka.loaders.JSONLoader;
import org.apache.kafka.streams.KafkaStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamManager {

    private HashMap<String, KafkaStreams> streamsMap;
    private final Logger log = LoggerFactory.getLogger(StreamManager.class);

    private static final class ManagerInstanceHolder {
        private static final StreamManager instance = new StreamManager();
    }

    public static StreamManager getInstance() {
        return ManagerInstanceHolder.instance;
    }

    public void initializeStreams() {
        log.info("Initializing streams");
        SystemConfiguration systemConfiguration = JSONLoader.loadConfig();
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
        streamsMap = new HashMap<>();
        GlobalConfig globalConfig = systemConfiguration.getGlobalConfig();
        for (AnonymizationStreamConfig streamConfig : streamConfigs) {
            streamsMap.put(streamConfig.getApplicationId(), AnonymizationStreamFactory.buildAnonymizationStream(globalConfig, streamConfig));
        }
        log.info("Streams created.");
        log.info("Starting all streams");
        startAllStreams();
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
        streamsMap.get(applicationId).start();
    }

    public void stopStream(String applicationId) {
        streamsMap.get(applicationId).close();
    }

    public void pauseStream(String applicationId) {
        streamsMap.get(applicationId).pause();
    }

    public void listStreams() {
        log.info("Listing all streams:");
        for (Map.Entry<String, KafkaStreams> stream : streamsMap.entrySet()) {
            log.info("Stream {} is in state {}", stream.getKey(), stream.getValue().state());
        }
    }
}
