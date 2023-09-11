package com.anonymization.kafka.anonymizers.valuebased;

import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.configs.stream.ParameterType;
import com.anonymization.kafka.validators.KeyValidator;
import com.anonymization.kafka.validators.ParameterExpectation;
import com.anonymization.kafka.validators.PositiveIntegerValidator;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class Bucketizing implements ValueBasedAnonymizer {

    private List<String> keysToBucketize = Collections.emptyList();
    private int bucketSize = 0;
    private final Logger log = LoggerFactory.getLogger(Bucketizing.class);
    @Override
    public List<Struct> anonymize(List<Struct> lineS) {
        if (lineS.size() != 1) {
            log.info("Value based anonymizer {} called with more than one line", getClass().getName());
            return null;
        }

        Struct originalStruct = lineS.get(0);
        SchemaBuilder schemaBuilder = SchemaBuilder.struct();

        for (Field field : originalStruct.schema().fields()) {
            if (keysToBucketize.contains(field.name())) {
                schemaBuilder.field(field.name(), Schema.STRING_SCHEMA);
            } else {
                schemaBuilder.field(field.name(), field.schema());
            }
        }

        Schema newSchema = schemaBuilder.build();
        Struct newStruct = new Struct(newSchema);

        for (Field field : originalStruct.schema().fields()) {
            if (keysToBucketize.contains(field.name())) {
                int keyValue = 0;
                try {
                    keyValue = originalStruct.getInt32(field.name());
                } catch (Exception e) {
                    log.error("Could not cast " + field.name() + " to an int for bucketizing operation.");
                }
                int l = keyValue / bucketSize;
                int h = l + 1;
                String result =  "[" + (l * bucketSize) + " - " + ((h * bucketSize) - 1) + "]";
                newStruct.put(field.name(), result);
            } else {
                newStruct.put(field.name(), originalStruct.get(field));
            }
        }

        return List.of(newStruct);
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
                    this.keysToBucketize = param.getKeys();
                    break;
                case BUCKET_SIZE:
                    this.bucketSize = param.getBucketSize();
                    break;
            }
        }
    }

    public Bucketizing() {
    }
}
