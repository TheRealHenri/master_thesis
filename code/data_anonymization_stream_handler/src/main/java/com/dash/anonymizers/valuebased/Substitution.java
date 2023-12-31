package com.dash.anonymizers.valuebased;

import com.dash.configs.stream.Parameter;
import com.dash.configs.stream.ParameterType;
import com.dash.validators.KeyValidator;
import com.dash.validators.ParameterExpectation;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Substitution implements ValueBasedAnonymizer {

    private List<String> keysToSubstitute = Collections.emptyList();
    private List<String> substitutionList = Collections.emptyList();
    private final Logger log = LoggerFactory.getLogger(Substitution.class);
    @Override
    public List<Struct> anonymize(List<Struct> lineS) {
        if (lineS.size() != 1) {
            log.info("Value based anonymizer {} called with more than one line", getClass().getName());
            return null;
        }

        Struct originalStruct = lineS.get(0);
        SchemaBuilder schemaBuilder = SchemaBuilder.struct();

        for (Field field : originalStruct.schema().fields()) {
            if (keysToSubstitute.contains(field.name())) {
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
        Random random = new Random();

        for (Field field : originalStruct.schema().fields()) {
            if (keysToSubstitute.contains(field.name())) {
                int randomIndex = random.nextInt(substitutionList.size());
                newStruct.put(field.name(), substitutionList.get(randomIndex));
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
                        ParameterType.SUBSTITUTION_LIST.getName(),
                        Collections.emptyList(),
                        true
                )
        );
    }

    @Override
    public void initialize(List<Parameter> parameters) {
        for (Parameter param : parameters) {
            switch (param.getType()) {
                case KEYS:
                    this.keysToSubstitute = param.getKeys();
                    break;
                case SUBSTITUTION_LIST:
                    this.substitutionList = param.getSubstitutionList();
                    break;
            }
        }
    }

    public Substitution() {
    }
}
