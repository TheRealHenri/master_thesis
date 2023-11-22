package com.dash.anonymizers.valuebased;

import com.dash.configs.stream.Parameter;
import com.dash.configs.stream.ParameterType;
import com.dash.validators.KeyValidator;
import com.dash.validators.ParameterExpectation;
import org.apache.kafka.connect.data.Struct;

import java.util.Collections;
import java.util.List;

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
            switch (param.getType()) {
                case KEYS:
                    keysToTokenize = param.getKeys();
                    break;
            }
        }
    }

    public Tokenization() {
    }
}
