package com.anonymization.kafka.anonymizers.valuebased;

import com.anonymization.kafka.configs.stream.Key;
import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.configs.stream.ParameterType;
import com.anonymization.kafka.validators.KeyValidator;
import com.anonymization.kafka.validators.ParameterExpectation;
import com.anonymization.kafka.validators.PositiveIntegerValidator;
import org.apache.kafka.connect.data.Struct;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Generalization implements ValueBasedAnonymizer {

    private List<Key> keysToGeneralize = Collections.emptyList();
    private Optional<List<String>> generalizationValues = Optional.empty();

    @Override
    public List<Struct> anonymize(List<Struct> lineS) {
        return lineS;
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
                        "generalizationValues",
                        List.of(new PositiveIntegerValidator()),
                        false
                )
        );
    }

    @Override
    public void initialize(List<Parameter> parameters) {
        for (Parameter param : parameters) {
            switch (param.getType()) {
                case KEYS:
                    this.keysToGeneralize = (List<Key>) param.getValue();
                    break;
            }
        }
    }

    public Generalization() {
    }
}
