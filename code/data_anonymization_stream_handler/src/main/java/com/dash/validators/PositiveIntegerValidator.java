package com.dash.validators;

import com.dash.configs.global.schemas.SchemaCommon;
import com.dash.configs.stream.Parameter;

public class PositiveIntegerValidator implements ParameterValidator{

    public PositiveIntegerValidator() {

    }

    @Override
    public void validateParameter(Parameter param, SchemaCommon schema) throws IllegalArgumentException {
        int intParam;
        try {
            intParam = param.toInt();
        } catch (Exception e) {
            throw new IllegalArgumentException("Provided Parameter " + param.getType() + " is not of required Type int");
        }
        if (intParam <= 0) {
            throw new IllegalArgumentException("Parameter " + param + " has to be a positive integer, but is " + intParam);
        }
    }
}
