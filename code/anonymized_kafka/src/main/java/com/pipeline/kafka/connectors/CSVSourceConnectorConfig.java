package com.pipeline.kafka.connectors;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class CSVSourceConnectorConfig extends AbstractConfig {

    public CSVSourceConnectorConfig(ConfigDef config, Map<?, ?> parsedConfig) {
        super(config, parsedConfig);
    }


}
