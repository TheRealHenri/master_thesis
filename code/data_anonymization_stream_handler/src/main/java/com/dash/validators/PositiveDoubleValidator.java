package com.dash.validators;

import com.dash.configs.global.schemas.SchemaCommon;
import com.dash.configs.stream.Parameter;

public class PositiveDoubleValidator implements ParameterValidator{

    public PositiveDoubleValidator() {
    }

    @Override
    public void validateParameter(Parameter param, SchemaCommon schema) throws IllegalArgumentException {
        double doubleParam;
        try {
            doubleParam = param.toDouble();
        } catch (Exception e) {
            throw new IllegalArgumentException("Provided Parameter " + param.getType() + " is not of required Type Double");
        }
        if (doubleParam <= 0) {
            throw new IllegalArgumentException("Parameter " + param + " has to be a positive double, but is " + doubleParam);
        }
    }
}
