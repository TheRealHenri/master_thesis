package com.anonymization.kafka.anonymizers.tuplebased;

import com.anonymization.kafka.AnonymizationCategory;
import com.anonymization.kafka.anonymizers.Anonymizer;
import com.anonymization.kafka.anonymizers.WindowConfig;

public interface TupleBasedAnonymizer extends Anonymizer {
    @Override
    default AnonymizationCategory getAnonymizationCategory() {
        return AnonymizationCategory.TUPLE_BASED;
    }
    @Override
    default WindowConfig getWindowConfig() { return null; }
}
