package com.dash.validators;

import com.dash.anonymizers.tablebased.datastructures.CategoricalHierarchy;
import com.dash.anonymizers.tablebased.datastructures.NumericalHierarchy;
import com.dash.configs.global.schemas.SchemaCommon;
import com.dash.configs.stream.Parameter;
import com.dash.configs.stream.ParameterType;
import com.dash.configs.stream.QuasiIdentifier;

import java.util.List;

public class QIKeysValidator implements ParameterValidator {

    public QIKeysValidator() {
    }

    @Override
    public void validateParameter(Parameter param, SchemaCommon schema) throws IllegalArgumentException {
        List<QuasiIdentifier> qis;
        try {
            qis = param.getQuasiIdentifiers();
        } catch (Exception e) {
            throw new IllegalArgumentException("Provided Parameter " + param.getType() + " is not of required Type QuasiIdentifier");
        }
        for (QuasiIdentifier qi : qis) {
            Parameter qiKey = new Parameter(ParameterType.KEYS, List.of(qi.getKey()));
            if (qi.getHierarchy() instanceof CategoricalHierarchy) {
                KeyValidator keyValidator = new KeyValidator();
                keyValidator.validateParameter(qiKey, schema);
            } else if (qi.getHierarchy() instanceof NumericalHierarchy) {
                KeyValidator keyValidator = new KeyValidator(true);
                keyValidator.validateParameter(qiKey, schema);
            } else {
                throw new IllegalArgumentException("QuasiIdentifier " + qi.getKey() + " has an invalid hierarchy");
            }
        }
    }
}
