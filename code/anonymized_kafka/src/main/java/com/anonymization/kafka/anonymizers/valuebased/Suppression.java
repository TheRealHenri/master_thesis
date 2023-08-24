package com.anonymization.kafka.anonymizers.valuebased;

import com.anonymization.kafka.configs.stream.Key;
import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.configs.stream.ParameterType;
import com.anonymization.kafka.validators.KeyValidator;
import com.anonymization.kafka.validators.ParameterExpectation;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Suppression implements ValueBasedAnonymizer {

    private List<Key> keysToSuppress = Collections.emptyList();

    @Override
    public String anonymize(String lineS) {
        return null;
    }

    @Override
    public List<ParameterExpectation> getParameterValidators() {
        return List.of(
                new ParameterExpectation(
                        "keys",
                        List.of(new KeyValidator()),
                        true
                )
        );
    }

    @Override
    public void initialize(List<Parameter> parameters) {
        for (Parameter param : parameters) {
            if (Objects.requireNonNull(param.getType()) == ParameterType.KEYS) {
                this.keysToSuppress = (List<Key>) param.getValue();
            }
        }
    }

    public Suppression() {
    }
}
