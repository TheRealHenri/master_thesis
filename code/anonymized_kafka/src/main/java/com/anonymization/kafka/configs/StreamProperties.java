package com.anonymization.kafka.configs;

import com.anonymization.kafka.AnonymizationCategory;

import java.util.Set;

public class StreamProperties {
    private final String applicationId;
    private final AnonymizationCategory category;
    private final Set<String> anonymizerNames;

    public StreamProperties(String applicationId, AnonymizationCategory category, Set<String> anonymizerNames) {
        this.applicationId = applicationId;
        this.category = category;
        this.anonymizerNames = anonymizerNames;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public AnonymizationCategory getCategory() {
        return category;
    }

    public Set<String> getAnonymizerNames() {
        return anonymizerNames;
    }
}
