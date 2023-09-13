package com.anonymization.kafka.anonymizers.tablebased;

import com.anonymization.kafka.AnonymizationCategory;
import com.anonymization.kafka.anonymizers.Anonymizer;

public interface TableBasedAnonymizer extends Anonymizer {
    @Override
    default AnonymizationCategory getAnonymizationCategory() {
        return AnonymizationCategory.TABLE_BASED;
    }
}
