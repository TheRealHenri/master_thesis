package com.anonymization.kafka.anonymizers.valuebased;

import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.configs.stream.ParameterType;
import com.anonymization.kafka.validators.KeyValidator;
import com.anonymization.kafka.validators.ParameterExpectation;
import org.apache.kafka.connect.data.Struct;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Tokenization implements ValueBasedAnonymizer {

    private List<String> keysToTokenize = Collections.emptyList();
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
                )
        );
    }

    @Override
    public void initialize(List<Parameter> parameters) {
        for (Parameter param : parameters) {
            if (Objects.requireNonNull(param.getType()) == ParameterType.KEYS) {
                this.keysToTokenize = (List<String>) param.getValue();
            }
        }
    }

    public Tokenization() {
    }
}
