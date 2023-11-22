package com.dash.validators;

import com.dash.configs.global.schemas.SchemaCommon;
import com.dash.configs.stream.Parameter;

public interface ParameterValidator {
    void validateParameter(Parameter param, SchemaCommon schema) throws IllegalArgumentException;
}
