package com.anonymization.kafka.anonymizers;

import com.anonymization.kafka.AnonymizationCategory;
import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.validators.ParameterExpectation;
import org.apache.kafka.connect.data.Struct;

import java.util.List;

public interface Anonymizer {
    List<Struct> anonymize(List<Struct> lineS);

    List<ParameterExpectation> getParameterExpectations();

    AnonymizationCategory getAnonymizationCategory();

    void initialize(List<Parameter> parameters);
    WindowConfig getWindowConfig();
}