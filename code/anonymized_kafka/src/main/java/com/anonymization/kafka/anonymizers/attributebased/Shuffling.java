package com.anonymization.kafka.anonymizers.attributebased;

import com.anonymization.kafka.anonymizers.WindowConfig;
import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.configs.stream.ParameterType;
import com.anonymization.kafka.validators.KeyValidator;
import com.anonymization.kafka.validators.ParameterExpectation;
import com.anonymization.kafka.validators.PositiveIntegerValidator;
import org.apache.kafka.connect.data.Struct;

import java.util.Collections;
import java.util.List;

public class Shuffling implements AttributeBasedAnonymizer {

    private List<String> keysToShuffle = Collections.emptyList();
    private int windowSize = 0;

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
                )
        );
    }

    @Override
    public void initialize(List<Parameter> parameters) {
        for (Parameter param : parameters) {
            switch (param.getType()) {
                case KEYS:
                    this.keysToShuffle = (List<String>) param.getValue();
                    break;
                case WINDOW_SIZE:
                    this.windowSize = param.toInt();
                    break;
            }
        }
    }

    public Shuffling() {
    }

    @Override
    public WindowConfig getWindowConfig() {
        return null;
    }
}
