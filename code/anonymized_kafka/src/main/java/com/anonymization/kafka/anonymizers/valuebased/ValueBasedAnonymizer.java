package com.anonymization.kafka.anonymizers.valuebased;

import com.anonymization.kafka.AnonymizationCategory;
import com.anonymization.kafka.anonymizers.Anonymizer;
import com.anonymization.kafka.anonymizers.WindowConfig;

public interface ValueBasedAnonymizer extends Anonymizer {
    @Override
    default AnonymizationCategory getAnonymizationCategory() {
        return AnonymizationCategory.VALUE_BASED;
    }
    @Override
    default WindowConfig getWindowConfig() { return null; }
}
