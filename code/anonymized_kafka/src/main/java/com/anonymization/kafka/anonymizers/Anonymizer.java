package com.anonymization.kafka.anonymizers;

import com.anonymization.kafka.AnonymizationCategory;
import com.anonymization.kafka.validators.ParameterValidator;

import java.util.Set;

public interface Anonymizer {
    public String anonymize(String lineS);

    public Set<ParameterValidator> getParameterValidators();

    public AnonymizationCategory getCategory();
}