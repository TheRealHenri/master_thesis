package com.anonymization.kafka.validators;

import com.anonymization.kafka.configs.global.schemas.SchemaCommon;
import com.anonymization.kafka.configs.stream.Parameter;

public class PositiveLongValidator implements ParameterValidator {
    public PositiveLongValidator() {
    }

    @Override
    public void validateParameter(Parameter param, SchemaCommon schema) throws IllegalArgumentException {
        long longParam;
        try {
            longParam = param.toLong();
        } catch (Exception e) {
            throw new IllegalArgumentException("Provided Parameter " + param.getType() + " is not of required Type Long");
        }
        if (longParam <= 0) {
            throw new IllegalArgumentException("Parameter " + param + " has to be a positive long, but is " + longParam);
        }
    }
}
