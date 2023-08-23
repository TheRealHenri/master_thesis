package com.anonymization.kafka.anonymizers;

import com.anonymization.kafka.AnonymizationCategory;
import com.anonymization.kafka.validators.ParameterValidator;

import java.util.Set;

public interface Anonymizer {
    String anonymize(String lineS);

    Set<ParameterValidator> getParameterValidators();

    AnonymizationCategory getAnonymizationCategory();
}