package com.anonymization.kafka.anonymizers.valuebased;

import com.anonymization.kafka.AnonymizationCategory;
import com.anonymization.kafka.anonymizers.Anonymizer;

public interface ValueBasedAnonymizer extends Anonymizer {
    @Override
    default AnonymizationCategory getAnonymizationCategory() {
        return AnonymizationCategory.VALUE_BASED;
    }
}
