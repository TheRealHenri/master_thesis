package com.anonymization.kafka.builders;

import com.anonymization.kafka.AnonymizationCategory;
import com.anonymization.kafka.anonymizers.Anonymizer;
import com.anonymization.kafka.configs.AnonymizationStreamConfig;
import com.anonymization.kafka.configs.StreamProperties;
import com.anonymization.kafka.registry.AnonymizerRegistry;
import org.apache.kafka.common.protocol.types.Schema;

public class AnonymizationStreamConfigBuilder {

    private Schema dataSchema;

    public AnonymizationStreamConfigBuilder(Schema dataSchema) {
        this.dataSchema = dataSchema;
    }

    public AnonymizationStreamConfig build(StreamProperties streamProperties) {

        AnonymizationCategory anonymizationCategory = null;

        for (String anonymizerName : streamProperties.getAnonymizerNames()) {
            Class<? extends Anonymizer> anonymizer = AnonymizerRegistry.getClassFrom(anonymizerName);
            // validate parameters
            // instantiate class
            // make sure category fits
        }
        return null;
    }
}
