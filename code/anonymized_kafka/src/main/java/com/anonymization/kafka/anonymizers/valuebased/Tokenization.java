package com.anonymization.kafka.anonymizers.valuebased;

import com.anonymization.kafka.validators.ParameterValidator;

import java.util.Set;

public class Tokenization implements ValueBasedAnonymizer {
    @Override
    public String anonymize(String lineS) {
        return null;
    }

    @Override
    public Set<ParameterValidator> getParameterValidators() {
        return null;
    }
}
