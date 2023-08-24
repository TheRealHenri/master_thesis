package com.anonymization.kafka.anonymizers.tablebased;

import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.validators.ParameterExpectation;
import com.anonymization.kafka.validators.PositiveIntegerValidator;

import java.util.List;

public class KAnonymization implements TableBasedAnonymizer {

    private int windowSize = 0;
    private int k = 0;

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
                ),
                new ParameterExpectation(
                        "k",
                        List.of(new PositiveIntegerValidator()),
                        true
                )
        );
    }

    @Override
    public void initialize(List<Parameter> parameters) {
        for (Parameter param : parameters) {
            switch (param.getType()) {
                case WINDOW_SIZE:
                    this.windowSize = param.toInt();
                    break;
                case K:
                    this.k = param.toInt();
                    break;
            }
        }
    }

    public KAnonymization() {}
}
