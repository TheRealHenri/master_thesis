package com.anonymization.kafka.anonymizers.tablebased;

import com.anonymization.kafka.anonymizers.WindowConfig;
import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.configs.stream.ParameterType;
import com.anonymization.kafka.validators.ParameterExpectation;
import com.anonymization.kafka.validators.PositiveIntegerValidator;
import org.apache.kafka.connect.data.Struct;

import java.util.List;

public class TCloseness implements TableBasedAnonymizer {
    private int windowSize = 0;
    private int k = 0;
    private int l = 0;
    private int t = 0;

    @Override
    public List<Struct> anonymize(List<Struct> lineS, int position) {
        return null;
    }
    @Override
    public List<Struct> anonymize(List<Struct> lineS) {
        return null;
    }

    @Override
    public List<ParameterExpectation> getParameterExpectations() {
        return List.of(
                new ParameterExpectation(
                        ParameterType.WINDOW_SIZE.getName(),
                        List.of(new PositiveIntegerValidator()),
                        true
                ),
                new ParameterExpectation(
                        ParameterType.K.getName(),
                        List.of(new PositiveIntegerValidator()),
                        true
                ),
                new ParameterExpectation(
                        ParameterType.L.getName(),
                        List.of(new PositiveIntegerValidator()),
                        true
                ),
                new ParameterExpectation(
                        ParameterType.T.getName(),
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

    @Override
    public WindowConfig getWindowConfig() {
        return null;
    }
}
