package com.anonymization.kafka.configs.stream;

import com.anonymization.kafka.AnonymizationCategory;

import java.util.Set;

public class StreamProperties {
    private  String applicationId;
    private AnonymizationCategory category;
    private  Set<AnonymizerConfig> anonymizers;

    public StreamProperties(){}
    public StreamProperties(String applicationId, AnonymizationCategory category, Set<AnonymizerConfig> anonymizers) {
        this.applicationId = applicationId;
        this.category = category;
        this.anonymizers = anonymizers;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public AnonymizationCategory getCategory() {
        return category;
    }

    public Set<AnonymizerConfig> getAnonymizers() {
        return anonymizers;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public void setCategory(AnonymizationCategory category) {
        this.category = category;
    }

    public void setAnonymizers(Set<AnonymizerConfig> anonymizers) {
        this.anonymizers = anonymizers;
    }
}
