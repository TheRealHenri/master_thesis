package com.pipeline.kafka.connectors;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class CSVSourceConnector extends SourceConnector {

    private static final Logger log = LoggerFactory.getLogger(CSVSourceConnector.class);
    private Map<String, String> configProperties;

    @Override
    public void start(Map<String, String> props) {
        log.info("Starting up CSV Source Connector");
        configProperties = props;
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
        List<Map<String, String>> configs = new ArrayList<>(maxTasks);
        Map<String, String> taskConfig = new HashMap<>(configProperties);
        configs.add(taskConfig);
        return configs;
    }

    @Override
    public void stop() {

    }

    @Override
    public ConfigDef config() {
        return CSVSourceConnectorConfig.conf();
    }

    @Override
    public String version() {
        return null;
    }
}
