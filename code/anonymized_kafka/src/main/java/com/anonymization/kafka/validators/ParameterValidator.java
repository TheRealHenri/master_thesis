package com.anonymization.kafka.validators;

public interface ParameterValidator {
    void validateParameters() throws IllegalArgumentException;
}
