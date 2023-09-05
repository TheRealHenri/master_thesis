package com.anonymization.kafka.anonymizers.tuplebased;

import com.anonymization.kafka.configs.stream.Key;
import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.configs.stream.ParameterType;
import com.anonymization.kafka.validators.KeyValidator;
import com.anonymization.kafka.validators.ParameterExpectation;
import com.anonymization.kafka.validators.PositiveIntegerValidator;
import org.apache.kafka.connect.data.Struct;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ConditionalSubstitution implements TupleBasedAnonymizer {

    private List<Key> keysToSubstitute = Collections.emptyList();
    private HashMap<Object, Object> lookupTable = new HashMap<>();
    @Override
    public Struct anonymize(List<Struct> lineS) {
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
                        "lookupTable",
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
                    this.keysToSubstitute = (List<Key>) parameter.getValue();
                    break;
            }
        }
    }

    public ConditionalSubstitution() {}
}
