package com.anonymization.kafka.anonymizers.attributebased;

import com.anonymization.kafka.AnonymizationCategory;
import com.anonymization.kafka.validators.ParameterValidator;

import java.util.Set;

public class Aggregation implements AttributeBasedAnonymizer {
    @Override
    public String anonymize(String lineS) {
        return null;
    }

    @Override
    public Set<ParameterValidator> getParameterValidators() {
        return null;
    }
}
