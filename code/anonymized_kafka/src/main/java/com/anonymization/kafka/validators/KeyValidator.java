package com.anonymization.kafka.validators;

import com.anonymization.kafka.configs.global.schemas.SchemaCommon;
import com.anonymization.kafka.configs.stream.Parameter;

import java.util.List;

public class KeyValidator implements ParameterValidator {

    public KeyValidator() {
    }

    @Override
    public void validateParameter(Parameter param, SchemaCommon schema) throws IllegalArgumentException {
        List<String> keys;
        try {
            keys = param.getKeys();
        } catch (Exception e) {
            throw new IllegalArgumentException("Provided Parameter " + param.getType() + " is not of required Type List<String>");
        }
        for (String key : keys) {
            if (!schema.getDataFields().containsKey(key)) {
                throw new IllegalArgumentException("Key " + key + " is not present in schema");
            }
        }
    }
}
