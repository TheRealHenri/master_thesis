package com.anonymization.kafka.validators;

import com.anonymization.kafka.configs.global.schemas.SchemaCommon;
import com.anonymization.kafka.configs.stream.Parameter;

public class PositiveIntegerValidator implements ParameterValidator{

    public PositiveIntegerValidator() {

    }

    @Override
    public void validateParameter(Parameter param, SchemaCommon schema) throws IllegalArgumentException {
        // do nothing
    }
}
