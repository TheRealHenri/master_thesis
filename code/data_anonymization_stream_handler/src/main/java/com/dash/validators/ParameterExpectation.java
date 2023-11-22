package com.dash.validators;

import com.dash.configs.global.schemas.SchemaCommon;
import com.dash.configs.stream.Parameter;

import java.util.List;

public class ParameterExpectation {
    private final String paramName;
    private final List<ParameterValidator> validators;
    private final boolean isRequired;

    public ParameterExpectation(String paramName, List<ParameterValidator> validators, boolean isRequired) {
        this.paramName = paramName;
        this.validators = validators;
        this.isRequired = isRequired;
    }

    public String getParamName() {
        return paramName;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void validate(Parameter param, SchemaCommon schema) throws IllegalArgumentException {
        for (ParameterValidator validator : validators) {
            validator.validateParameter(param, schema);
        }
    }
}
