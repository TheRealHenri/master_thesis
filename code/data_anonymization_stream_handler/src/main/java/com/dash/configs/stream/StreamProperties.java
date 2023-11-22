package com.dash.configs.stream;

import com.dash.AnonymizationCategory;

import java.util.List;

public class StreamProperties {
    private  String applicationId;
    private AnonymizationCategory category;
    private List<AnonymizerConfig> anonymizers;

    public StreamProperties(){}
    public StreamProperties(String applicationId, AnonymizationCategory category, List<AnonymizerConfig> anonymizers) {
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

    public List<AnonymizerConfig> getAnonymizers() {
        return anonymizers;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public void setCategory(AnonymizationCategory category) {
        this.category = category;
    }

    public void setAnonymizers(List<AnonymizerConfig> anonymizers) {
        this.anonymizers = anonymizers;
    }
}
