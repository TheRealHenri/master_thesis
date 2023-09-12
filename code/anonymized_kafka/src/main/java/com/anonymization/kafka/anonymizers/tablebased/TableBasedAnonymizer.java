package com.anonymization.kafka.anonymizers.tablebased;

import com.anonymization.kafka.AnonymizationCategory;
import com.anonymization.kafka.anonymizers.Anonymizer;
import com.anonymization.kafka.anonymizers.window.WindowConfig;

public interface TableBasedAnonymizer extends Anonymizer {
    @Override
    default AnonymizationCategory getAnonymizationCategory() {
        return AnonymizationCategory.TABLE_BASED;
    }
}
