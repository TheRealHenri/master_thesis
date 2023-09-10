package com.anonymization.kafka.anonymizers.valuebased;

import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.configs.stream.ParameterType;
import com.anonymization.kafka.validators.KeyValidator;
import com.anonymization.kafka.validators.ParameterExpectation;
import com.anonymization.kafka.validators.PositiveIntegerValidator;
import org.apache.kafka.connect.data.Struct;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Substitution implements ValueBasedAnonymizer {

    private List<String> keysToSubstitute = Collections.emptyList();
    private Optional<List<String>> mappingTable = Optional.empty();
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
                        "mappingTable",
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
                    this.keysToSubstitute = (List<String>) param.getValue();
                    break;
            }
        }
    }

    public Substitution() {
    }
}
