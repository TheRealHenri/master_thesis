package com.dash.anonymizers;

import com.dash.AnonymizationCategory;
import com.dash.configs.stream.Parameter;
import com.dash.validators.ParameterExpectation;
import org.apache.kafka.connect.data.Struct;

import java.util.List;

public interface Anonymizer {
    List<Struct> anonymize(List<Struct> lineS);

    List<ParameterExpectation> getParameterExpectations();

    AnonymizationCategory getAnonymizationCategory();

    void initialize(List<Parameter> parameters);
    WindowConfig getWindowConfig();
}