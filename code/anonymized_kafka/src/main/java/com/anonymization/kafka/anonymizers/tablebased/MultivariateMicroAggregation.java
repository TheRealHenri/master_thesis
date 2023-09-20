package com.anonymization.kafka.anonymizers.tablebased;

import com.anonymization.kafka.anonymizers.WindowConfig;
import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.configs.stream.ParameterType;
import com.anonymization.kafka.validators.KeyValidator;
import com.anonymization.kafka.validators.ParameterExpectation;
import com.anonymization.kafka.validators.PositiveIntegerValidator;
import org.apache.kafka.connect.data.Struct;

import java.util.Collections;
import java.util.List;

public class MultivariateMicroAggregation implements TableBasedAnonymizer {

    private List<String> keysToAggregate = Collections.emptyList();
    private int windowSize = 0;
    private int groupSize = 0;

    @Override
    public List<Struct> anonymize(List<Struct> lineS) {
        return null;
    }

    @Override
    public List<ParameterExpectation> getParameterExpectations() {
        return List.of(
                new ParameterExpectation(
                        ParameterType.KEYS.getName(),
                        List.of(new KeyValidator()),
                        true
                ),
                new ParameterExpectation(
                        ParameterType.WINDOW_SIZE.getName(),
                        List.of(new PositiveIntegerValidator()),
                        true
                ),
                new ParameterExpectation(
                        ParameterType.GROUP_SIZE.getName(),
                        List.of(new PositiveIntegerValidator()),
                        true
                )
        );
    }

    @Override
    public void initialize(List<Parameter> parameters) {
        for (Parameter parameter : parameters) {
            switch (parameter.getType()) {
                case KEYS:
                    this.keysToAggregate = (List<String>) parameter.getValue();
                    break;
                case WINDOW_SIZE:
                    this.windowSize = parameter.toInt();
                    break;
                case GROUP_SIZE:
                    this.groupSize = parameter.toInt();
                    break;
            }
        }
    }

    public MultivariateMicroAggregation() {}

    @Override
    public WindowConfig getWindowConfig() {
        return null;
    }
}
