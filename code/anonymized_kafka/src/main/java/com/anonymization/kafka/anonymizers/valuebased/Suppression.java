package com.anonymization.kafka.anonymizers.valuebased;

import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.configs.stream.ParameterType;
import com.anonymization.kafka.validators.KeyValidator;
import com.anonymization.kafka.validators.ParameterExpectation;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Suppression implements ValueBasedAnonymizer {

    private List<String> keysToSuppress = Collections.emptyList();
    private final Logger log = LoggerFactory.getLogger(Suppression.class);

    @Override
    public List<Struct> anonymize(List<Struct> lineS) {
        if (lineS.size() != 1) {
            log.info("Value based anonymizer {} called with more than one line", getClass().getName());
            return null;
        }

        Struct originalStruct = lineS.get(0);
        SchemaBuilder schemaBuilder = SchemaBuilder.struct();

        for (Field field : originalStruct.schema().fields()) {
            if (keysToSuppress.contains(field.name())) {
                if (field.schema().equals(Schema.STRING_SCHEMA) || field.schema().equals(Schema.OPTIONAL_STRING_SCHEMA)) {
                    schemaBuilder.field(field.name(), field.schema());
                } else {
                    schemaBuilder.field(field.name(), Schema.STRING_SCHEMA);
                }
            } else {
                schemaBuilder.field(field.name(), field.schema());
            }
        }

        Schema newSchema = schemaBuilder.build();
        Struct newStruct = new Struct(newSchema);

        for (Field field : originalStruct.schema().fields()) {
            if (keysToSuppress.contains(field.name())) {
                newStruct.put(field.name(), "*");
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
                )
        );
    }

    @Override
    public void initialize(List<Parameter> parameters) {
        for (Parameter param : parameters) {
            if (Objects.requireNonNull(param.getType()) == ParameterType.KEYS) {
                this.keysToSuppress = param.getKeys();
            }
        }
    }

    public Suppression() {
    }
}
