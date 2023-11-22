package com.dash.validators;

import com.dash.configs.global.schemas.SchemaCommon;
import com.dash.configs.stream.Parameter;

public class EnumValidator implements ParameterValidator {

    private final Class<? extends ParsableEnum> parsableEnum;

    public EnumValidator(Class<? extends ParsableEnum> parsableEnum) {
        this.parsableEnum = parsableEnum;
    }


    @Override
    public void validateParameter(Parameter param, SchemaCommon schema) throws IllegalArgumentException {
        ParsableEnum[] enumConstants = parsableEnum.getEnumConstants();
        for (ParsableEnum constant : enumConstants) {
            if (constant.getName().equals(param.getValue().toString())) {
                return;
            }
        }
        throw new IllegalArgumentException("Enum " + parsableEnum.getName() + " does not support " + param.getValue().toString());
    }
}
