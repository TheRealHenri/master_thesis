package com.pipeline.kafka.connectors;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.pipeline.kafka.utils.ConfigConstants.*;


public class CSVSourceConnector extends SourceConnector {

    private static final Logger log = LoggerFactory.getLogger(CSVSourceConnector.class);
    public static final String TOPIC_CONFIG = "topic";
    public static final String FILE_CONFIG = "file";
    public static final String TASK_BATCH_SIZE_CONFIG = "batch.size";
    public static final String TASK_LINGER_MS = "linger.ms";

    static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(TOPIC_CONFIG, ConfigDef.Type.STRING, DEFAULT_TOPIC, ConfigDef.Importance.HIGH, "Topic to write to")
            .define(FILE_CONFIG, ConfigDef.Type.STRING, PATH_TO_CSV, ConfigDef.Importance.HIGH, "File to read data from")
            .define(TASK_BATCH_SIZE_CONFIG, ConfigDef.Type.INT, DEFAULT_BATCH_SIZE, ConfigDef.Importance.LOW, "Batch Size")
            .define(TASK_LINGER_MS, ConfigDef.Type.INT, LINGER_MS, ConfigDef.Importance.LOW, "Linger time in ms");
    private Map<String, String> configProperties;

    @Override
    public void start(Map<String, String> props) {
        configProperties = props;
        AbstractConfig config = new AbstractConfig(CONFIG_DEF, props);
        String filePath = config.getString(FILE_CONFIG);
        log.info("Starting up CSV Source Connector");
        log.info("Reading from file {}", filePath);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return CSVSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        if (maxTasks != 1) {
            log.info("Ignoring maxTasks setting, only one task will be created.");
        }
        ArrayList<Map<String, String>> configs = new ArrayList<>(maxTasks);
        configs.add(configProperties);
        return configs;
    }

    @Override
    public void stop() {
        // Nothing to be done here
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    @Override
    public String version() {
        // Silly but w.e.
        return "1.0";
    }
}
