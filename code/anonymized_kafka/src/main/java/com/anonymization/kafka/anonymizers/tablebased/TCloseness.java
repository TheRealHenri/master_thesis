package com.anonymization.kafka.anonymizers.tablebased;

import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.validators.ParameterExpectation;
import com.anonymization.kafka.validators.PositiveIntegerValidator;

import java.util.List;

public class TCloseness implements TableBasedAnonymizer {
    private int windowSize = 0;
    private int k = 0;
    private int l = 0;
    private int t = 0;

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
                ),
                new ParameterExpectation(
                        "l",
                        List.of(new PositiveIntegerValidator()),
                        true
                ),
                new ParameterExpectation(
                        "t",
                        List.of(new PositiveIntegerValidator()),
                        true
                )
        );
    }

    @Override
    public void initialize(List<Parameter> parameters) {
        for (Parameter parameter : parameters) {
            switch (parameter.getType()) {
                case WINDOW_SIZE:
                    this.windowSize = parameter.toInt();
                    break;
                case K:
                    this.k = parameter.toInt();
                    break;
                case L:
                    this.l = parameter.toInt();
                    break;
                case T:
                    this.t = parameter.toInt();
                    break;
            }
        }
    }

    public TCloseness() {}
}
