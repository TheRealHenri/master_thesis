package com.anonymization.kafka.anonymizers.valuebased;

import com.anonymization.kafka.configs.stream.Key;
import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.configs.stream.ParameterType;
import com.anonymization.kafka.validators.KeyValidator;
import com.anonymization.kafka.validators.ParameterExpectation;
import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Blurring implements ValueBasedAnonymizer {

    private List<Key> keysToBlur = Collections.emptyList();
    private final Logger log = LoggerFactory.getLogger(Blurring.class);
    @Override
    public List<Struct> anonymize(List<Struct> lineS) {
        if (lineS.size() != 1) {
            log.info("Value based anonymizer {} called with more than one line", getClass().getName());
            return null;
        }
        Struct struct = lineS.get(0);
        for (Key key : keysToBlur) {
            struct.put(key.getKey(), "XXXXXX");
        }
        return List.of(struct);
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
                this.keysToBlur = (List<Key>) param.getValue();
            }
        }
    }

    public Blurring() {
    }
}
