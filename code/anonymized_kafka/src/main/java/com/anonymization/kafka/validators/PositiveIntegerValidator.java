package com.anonymization.kafka.validators;

import com.anonymization.kafka.configs.global.schemas.SchemaCommon;
import com.anonymization.kafka.configs.stream.Parameter;

public class PositiveIntegerValidator implements ParameterValidator{

    public PositiveIntegerValidator() {

    }

    @Override
    public void validateParameter(Parameter param, SchemaCommon schema) throws IllegalArgumentException {
        int intParam;
        try {
            intParam = param.toInt();
        } catch (Exception e) {
            throw new IllegalArgumentException("Provided Parameter " + param.getType() + " is not of required Type Double");
        }
        if (intParam <= 0) {
            throw new IllegalArgumentException("Parameter " + param + " has to be a positive integer, but is " + intParam);
        }
    }
}
