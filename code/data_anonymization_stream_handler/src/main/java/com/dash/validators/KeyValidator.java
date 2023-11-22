package com.dash.validators;

import com.dash.configs.global.schemas.FieldType;
import com.dash.configs.global.schemas.SchemaCommon;
import com.dash.configs.stream.Parameter;

import java.util.List;

public class KeyValidator implements ParameterValidator {

    private boolean requiresNumberValueForKey = false;

    public KeyValidator() {
    }

    public KeyValidator(boolean requiresNumberValueForKey) {
        this.requiresNumberValueForKey = requiresNumberValueForKey;
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
            } else if (requiresNumberValueForKey) {
                FieldType type = schema.getDataFields().get(key);
                if (!isNumber(type)) {
                    throw new IllegalArgumentException("Schema for key " + key + " requires a Number Schema, but found " + type);
                }
            }
        }
    }

    private boolean isNumber(FieldType type) {
        switch (type) {
            case INT:
            case OPTIONAL_INT:
            case LONG:
            case OPTIONAL_LONG:
            case FLOAT:
            case OPTIONAL_FLOAT:
            case DOUBLE:
            case OPTIONAL_DOUBLE:
                return true;
        }
        return false;
    }
}
