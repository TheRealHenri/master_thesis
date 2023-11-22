package com.dash.validators;

import com.dash.configs.global.schemas.SchemaCommon;
import com.dash.configs.stream.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ConditionMapValidator implements ParameterValidator {

    public ConditionMapValidator() {
    }

    @Override
    public void validateParameter(Parameter param, SchemaCommon schema) throws IllegalArgumentException {
        HashMap<String, Object> conditionMap;
        try {
            conditionMap = param.getConditionMap();
        } catch (Exception e) {
            throw new IllegalArgumentException("Provided Parameter " + param.getType() + " is not of required Type HashMap<String, Object>");
        }

        if (conditionMap.isEmpty()){
            throw new IllegalArgumentException(param.getType() + " cannot be empty. Provide at least one of matchValue, matchRange or matchRegex");
        }

        Set<String> allowedKeys = Set.of("matchValue", "matchRange", "matchRegex");
        for (String key : conditionMap.keySet()) {
            if (!allowedKeys.contains(key)) {
                throw new IllegalArgumentException("Provided key " + key + " in " + param.getType() + " is not equal to any of matchValue, matchRange or matchRegex");
            }
        }

        if (conditionMap.containsKey("matchRegex")) {
            try {
                Pattern.compile((String) conditionMap.get("matchRegex"));
            } catch (PatternSyntaxException e) {
                throw new IllegalArgumentException("Provided Regex " + conditionMap.get("matchRegex") + " is not a valid Regex.");
            }
        }

        if (conditionMap.containsKey("matchRange")) {
            ArrayList<Number> range;
            try {
                range = (ArrayList<Number>) conditionMap.get("matchRange");
            } catch (Exception e) {
                throw new IllegalArgumentException("Provided Range " + conditionMap.get("matchRange") + " does not consist of numbers");
            }
            if (range.size() != 2) {
                throw new IllegalArgumentException("Provided Range " + range + " does not consist of exactly two numbers");
            }
        }
    }
}
