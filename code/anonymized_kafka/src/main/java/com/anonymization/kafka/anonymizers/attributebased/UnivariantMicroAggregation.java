package com.anonymization.kafka.anonymizers.attributebased;

import com.anonymization.kafka.configs.stream.Key;
import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.configs.stream.ParameterType;
import com.anonymization.kafka.validators.KeyValidator;
import com.anonymization.kafka.validators.ParameterExpectation;
import com.anonymization.kafka.validators.PositiveIntegerValidator;
import org.apache.kafka.connect.data.Struct;

import java.util.Collections;
import java.util.List;

public class UnivariantMicroAggregation implements AttributeBasedAnonymizer {

    private List<Key> keysToAggregate = Collections.emptyList();
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
        for (Parameter param : parameters) {
            switch (param.getType()) {
                case KEYS:
                    this.keysToAggregate = (List<Key>) param.getValue();
                    break;
                case WINDOW_SIZE:
                    this.windowSize = param.toInt();
                    break;
                case GROUP_SIZE:
                    this.groupSize = param.toInt();
                    break;
            }
        }
    }

    public UnivariantMicroAggregation() {
    }
}
