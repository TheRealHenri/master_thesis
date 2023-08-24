package com.anonymization.kafka.anonymizers.tablebased;

import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.configs.stream.ParameterType;
import com.anonymization.kafka.validators.ParameterExpectation;
import com.anonymization.kafka.validators.PositiveIntegerValidator;

import java.util.List;
import java.util.Objects;

public class EpsPrivacy implements TableBasedAnonymizer {

    private int windowSize = 0;

    @Override
    public String anonymize(String lineS) {
        return null;
    }

    @Override
    public List<ParameterExpectation> getParameterValidators() {
        return List.of(
                new ParameterExpectation(
                        "windowSize",
                        List.of(new PositiveIntegerValidator()),
                        true
                )
        );
    }

    @Override
    public void initialize(List<Parameter> parameters) {
        for (Parameter param : parameters) {
            if (Objects.requireNonNull(param.getType()) == ParameterType.WINDOW_SIZE) {
                this.windowSize = param.toInt();
            }
        }
    }

    public EpsPrivacy() {}
}
