package com.anonymization.kafka.anonymizers;

import com.anonymization.kafka.AnonymizationCategory;
import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.validators.ParameterExpectation;

import java.util.List;

public interface Anonymizer {
    String anonymize(String lineS);

    List<ParameterExpectation> getParameterValidators();

    AnonymizationCategory getAnonymizationCategory();

    void initialize(List<Parameter> parameters);
}