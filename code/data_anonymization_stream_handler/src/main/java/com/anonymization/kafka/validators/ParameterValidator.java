package com.anonymization.kafka.validators;

import com.anonymization.kafka.configs.global.schemas.SchemaCommon;
import com.anonymization.kafka.configs.stream.Parameter;

public interface ParameterValidator {
    void validateParameter(Parameter param, SchemaCommon schema) throws IllegalArgumentException;
}
