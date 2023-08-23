package com.anonymization.kafka.configs;

import com.anonymization.kafka.AnonymizationCategory;
import com.anonymization.kafka.anonymizers.Anonymizer;
import com.anonymization.kafka.streams.StreamProcessingStrategy;

import java.util.Set;

public class AnonymizationStreamConfig {
    private final String applicationId;
    private final Set<Anonymizer> anonymizers;
    private final AnonymizationCategory category;
    private final StreamProcessingStrategy strategy;

    public AnonymizationStreamConfig(String applicationId, Set<Anonymizer> anonymizers, AnonymizationCategory category,  StreamProcessingStrategy strategy) {
        this.applicationId = applicationId;
        this.anonymizers = anonymizers;
        this.category = category;
        this.strategy = strategy;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public Set<Anonymizer> getAnonymizers() {
        return anonymizers;
    }

    public AnonymizationCategory getCategory() {
        return category;
    }

    public StreamProcessingStrategy getStrategy() {
        return strategy;
    }
}
