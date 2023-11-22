package com.dash.anonymizers.tuplebased;

import com.dash.configs.stream.Parameter;
import com.dash.configs.stream.ParameterType;
import com.dash.validators.ConditionMapValidator;
import com.dash.validators.KeyValidator;
import com.dash.validators.ParameterExpectation;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionalSubstitution implements TupleBasedAnonymizer {

    private List<String> keysToSubstitute = Collections.emptyList();
    private HashMap<String, Object> conditionMap = new HashMap<>();
    private List<String> substitutionList = Collections.emptyList();
    private final Logger log = LoggerFactory.getLogger(ConditionalSubstitution.class);
    @Override
    public List<Struct> anonymize(List<Struct> lineS) {
        if (lineS.size() != 1) {
            log.info("Tuple based anonymizer {} called with more than one line", getClass().getName());
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
                Object originalValue = originalStruct.get(field);
                boolean replace = false;

                if (conditionMap.containsKey("matchValue")) {
                    if (originalValue.equals(conditionMap.get("matchValue"))) {
                        replace = true;
                    }
                }

                if (!replace && conditionMap.containsKey("matchRange")) {
                    ArrayList<Number> matchRange = (ArrayList<Number>) conditionMap.get("matchRange");
                    if (originalValue instanceof Number) {
                        double originalNumber = ((Number) originalValue).doubleValue();
                        if (originalNumber >= matchRange.get(0).doubleValue() &&
                                originalNumber <= matchRange.get(1).doubleValue()) {
                            replace = true;
                        }
                    }
                }

                if (!replace && conditionMap.containsKey("matchRegex")) {
                    Pattern regex = Pattern.compile((String) conditionMap.get("matchRegex"));
                    Matcher matcher = regex.matcher(originalValue.toString());
                    if (matcher.matches()) {
                        replace = true;
                    }
                }

                if (replace) {
                    int randomIndex = random.nextInt(substitutionList.size());
                    newStruct.put(field.name(), substitutionList.get(randomIndex));
                } else {
                    newStruct.put(field.name(), originalValue.toString());
                }
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
                        ParameterType.CONDITION_MAP.getName(),
                        List.of(new ConditionMapValidator()),
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
        for (Parameter parameter : parameters) {
            switch (parameter.getType()) {
                case KEYS:
                    this.keysToSubstitute = parameter.getKeys();
                    break;
                case CONDITION_MAP:
                    this.conditionMap = parameter.getConditionMap();
                    break;
                case SUBSTITUTION_LIST:
                    this.substitutionList = parameter.getSubstitutionList();
                    break;
            }
        }
    }

    public ConditionalSubstitution() {}
}
