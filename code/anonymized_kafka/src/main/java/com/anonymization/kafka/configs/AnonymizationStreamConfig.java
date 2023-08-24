package com.anonymization.kafka.configs;

import com.anonymization.kafka.AnonymizationCategory;
import com.anonymization.kafka.anonymizers.Anonymizer;

import java.util.List;

public class AnonymizationStreamConfig {
    private final String applicationId;
    private final List<Anonymizer> anonymizers;
    private final AnonymizationCategory category;

    public AnonymizationStreamConfig(String applicationId, List<Anonymizer> anonymizers, AnonymizationCategory category) {
        this.applicationId = applicationId;
        this.anonymizers = anonymizers;
        this.category = category;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public List<Anonymizer> getAnonymizers() {
        return anonymizers;
    }

    public AnonymizationCategory getCategory() {
        return category;
    }
}
