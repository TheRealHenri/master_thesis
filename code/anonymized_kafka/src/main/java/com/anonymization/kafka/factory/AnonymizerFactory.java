package com.anonymization.kafka.factory;

import com.anonymization.kafka.AnonymizationCategory;
import com.anonymization.kafka.anonymizers.Anonymizer;

import java.util.Set;

public class AnonymizerFactory {

    private AnonymizationCategory category;

    public AnonymizerFactory(AnonymizationCategory category) {
        this.category = category;
    }

    public Set<Anonymizer> getAnonymizers() {
        return null;
    }
}
