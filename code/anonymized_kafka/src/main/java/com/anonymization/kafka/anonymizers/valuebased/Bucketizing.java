package com.anonymization.kafka.anonymizers.valuebased;

import com.anonymization.kafka.configs.stream.Key;
import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.configs.stream.ParameterType;
import com.anonymization.kafka.validators.KeyValidator;
import com.anonymization.kafka.validators.ParameterExpectation;
import com.anonymization.kafka.validators.PositiveIntegerValidator;
import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class Bucketizing implements ValueBasedAnonymizer {

    private List<Key> keysToBucketize = Collections.emptyList();
    private int bucketSize = 0;
    private final Logger log = LoggerFactory.getLogger(Bucketizing.class);
    @Override
    public List<Struct> anonymize(List<Struct> lineS) {
        if (lineS.size() != 1) {
            log.info("Value based anonymizer {} called with more than one line", getClass().getName());
            return null;
        }
        Struct struct = lineS.get(0);
        for (Key key : keysToBucketize) {
            int keyValue = 0;
            try {
                keyValue = Integer.parseInt((String) struct.get(key.getKey()));
            } catch (Exception e) {
                log.error("Could not cast " + key + " to an int for bucketizing operation.");
            }
            int l = keyValue / bucketSize;
            int h = l + 1;
            String result =  "[" + (l * bucketSize) + " - " + ((h * bucketSize) - 1) + "]";
            struct.put(key.getKey(), result);
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
                ),
                new ParameterExpectation(
                        ParameterType.BUCKET_SIZE.getName(),
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
                    this.keysToBucketize = (List<Key>) param.getValue();
                    break;
                case BUCKET_SIZE:
                    this.bucketSize = (int) param.getValue();
                    break;
            }
        }
    }

    public Bucketizing() {
    }
}
