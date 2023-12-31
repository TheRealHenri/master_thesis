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
import java.util.HashMap;
import java.util.List;

public class Generalization implements ValueBasedAnonymizer {

    private List<String> keysToGeneralize = Collections.emptyList();
    private HashMap<String, String> map = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(Generalization.class);

    @Override
    public List<Struct> anonymize(List<Struct> lineS) {
        if (lineS.size() != 1) {
            log.info("Value based anonymizer {} called with more than one line", getClass().getName());
            return null;
        }

        Struct originalStruct = lineS.get(0);
        SchemaBuilder schemaBuilder = SchemaBuilder.struct();

        for (Field field : originalStruct.schema().fields()) {
            if (keysToGeneralize.contains(field.name())) {
                schemaBuilder.field(field.name(), Schema.STRING_SCHEMA);
            } else {
                schemaBuilder.field(field.name(), field.schema());
            }
        }

        Schema newSchema = schemaBuilder.build();
        Struct newStruct = new Struct(newSchema);

        for (Field field : originalStruct.schema().fields()) {
            if (keysToGeneralize.contains(field.name())) {
                String structValue = originalStruct.get(field).toString();
                String result;
                try {
                    if (field.name().equals("zip")) {
                        // hardcoding for research
                        assert map.containsKey(structValue.substring(0, 1));
                        result = map.get(structValue.substring(0, 1));
                    } else {
                        assert map.containsKey(structValue);
                        result = map.get(structValue);
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException("Provided map does not cover all scenarios. " +
                                                       "Value " + structValue + " was given but is not included in " +
                                                       "map " + map);
                }
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
                        ParameterType.GENERALIZATION_MAP.getName(),
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
                    this.keysToGeneralize = param.getKeys();
                    break;
                case GENERALIZATION_MAP:
                    this.map = param.getGeneralizationMap();
                    break;
            }
        }
    }

    public Generalization() {
    }
}
