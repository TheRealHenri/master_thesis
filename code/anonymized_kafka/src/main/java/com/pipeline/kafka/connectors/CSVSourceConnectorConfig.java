package com.pipeline.kafka.connectors;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class CSVSourceConnectorConfig extends AbstractConfig {

    public static final String CSV_SETTING_CONFIG = "csv.setting.config";
    public static final String CSV_SETTING_DOC = "This is a setting important to my connector.";

    public CSVSourceConnectorConfig(ConfigDef config, Map<?, ?> parsedConfig) {
        super(config, parsedConfig);
    }

    public CSVSourceConnectorConfig(Map<?, ?> parsedConfig) {
        this(conf(), parsedConfig);
    }


    public static ConfigDef conf() {
        return new ConfigDef()
                .define(CSV_SETTING_CONFIG, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, CSV_SETTING_DOC);
    }

    public String getMy() {
        return this.getString(CSV_SETTING_CONFIG);
    }
}
