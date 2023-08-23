package com.anonymization.kafka.validators;

import org.apache.kafka.common.protocol.types.Schema;

public class KeyValidator implements ParameterValidator {
    private Schema dataSchema;

    public KeyValidator(Schema dataSchema) {
        this.dataSchema = dataSchema;
    }

    @Override
    public void validateParameters() throws IllegalArgumentException {
        validateKey();
    }

    private void validateKey() {
        // validate key lolz
    }
}
