package com.anonymization.kafka.anonymizers.valuebased;

import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.configs.stream.ParameterType;
import com.anonymization.kafka.validators.ParameterExpectation;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class NoiseMethods implements ValueBasedAnonymizer {

    private Double noise;
    @Override
    public String anonymize(String lineS) {
        return null;
    }

    @Override
    public List<ParameterExpectation> getParameterValidators() {
        return List.of(
                new ParameterExpectation(
                        "noise",
                        Collections.emptyList(),
                        true
                )
        );
    }

    @Override
    public void initialize(List<Parameter> parameters) {
        for (Parameter parameter : parameters) {
            if (Objects.requireNonNull(parameter.getType()) == ParameterType.NOISE) {
                this.noise = parameter.toDouble();
            }
        }
    }

    public NoiseMethods() {
    }
}
