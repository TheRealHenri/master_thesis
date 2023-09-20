package com.anonymization.kafka;

import com.anonymization.kafka.anonymizers.Anonymizer;
import com.anonymization.kafka.anonymizers.attributebased.UnivariateMicroAggregation;
import com.anonymization.kafka.builders.AnonymizationStreamConfigBuilder;
import com.anonymization.kafka.configs.AnonymizationStreamConfig;
import com.anonymization.kafka.configs.SystemConfiguration;
import com.anonymization.kafka.configs.global.GlobalConfig;
import com.anonymization.kafka.configs.global.schemas.SchemaCommon;
import com.anonymization.kafka.configs.stream.StreamProperties;
import com.anonymization.kafka.factory.AnonymizationStreamFactory;
import com.anonymization.kafka.loaders.JSONLoader;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.streams.KafkaStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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

    private void testStuff(List<AnonymizationStreamConfig> configs) {
        List<Double> mockData = Arrays.asList(1.0, 2.5, 2.0, 3.5, 4.0, 5.0, 7.0, 6.0, 5.5);

        UnivariateMicroAggregation testAnon = null;

        for (AnonymizationStreamConfig config : configs) {
            if (config.getApplicationId().equals("level_4")) {
                List<Anonymizer> anonymizers = config.getAnonymizers();
                for (Anonymizer anonymizer : anonymizers) {
                    if (anonymizer instanceof UnivariateMicroAggregation) {
                        testAnon = (UnivariateMicroAggregation) anonymizer;
                    }
                }
            }
        }

        if (testAnon == null) {
            return;
        }

        List<Struct> structs = getStructsForList(mockData);
        List<Struct> resultingStructe = testAnon.anonymize(structs);
        // print HbA1C values for all of the struct
        for (Struct struct : resultingStructe) {
            log.info("HbA1C value: {}", struct.getFloat64("HbA1C"));
        }
    }

    private List<Struct> getStructsForList (List<Double> doubleList) {
        List<Struct> result = new ArrayList<>();
        for (Double value : doubleList) {
            result.add(getStructForDouble(value));
        }
        return result;
    }

    private Struct getStructForDouble(Double value) {
        Struct struct = new Struct(SYNTHETIC_DATA_CSV_SCHEMA);
        struct.put("HbA1C", value);
        return struct;
    }

    public static final Schema SYNTHETIC_DATA_CSV_SCHEMA = SchemaBuilder.struct()
            .name("com.pipeline.kafka.connectors.SyntheticData")
            .field("id", Schema.INT32_SCHEMA)
            .field("name", Schema.STRING_SCHEMA)
            .field("address", Schema.STRING_SCHEMA)
            .field("zip", Schema.INT32_SCHEMA)
            .field("phone", Schema.STRING_SCHEMA)
            .field("gender", Schema.STRING_SCHEMA)
            .field("height", Schema.INT32_SCHEMA)
            .field("weight", Schema.INT32_SCHEMA)
            .field("age", Schema.INT32_SCHEMA)
            .field("insurance_company", Schema.INT32_SCHEMA)
            .field("insurance_number", Schema.STRING_SCHEMA)
            .field("diagnosis", Schema.STRING_SCHEMA)
            .field("glucose", Schema.INT32_SCHEMA)
            .field("HbA1C", Schema.FLOAT32_SCHEMA)
            .field("medication", Schema.STRING_SCHEMA)
            .build();
}
