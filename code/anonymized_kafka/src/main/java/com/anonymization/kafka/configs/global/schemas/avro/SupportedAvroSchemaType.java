package com.anonymization.kafka.configs.global.schemas.avro;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SupportedAvroSchemaType {
    BOOLEAN("boolean"),
    INT("int"),
    LONG("long"),
    FLOAT("float"),
    DOUBLE("double"),
    BYTES("bytes"),
    STRING("string"),
    NULL("null");

    private final String jsonType;

    SupportedAvroSchemaType(String jsonType) {
        this.jsonType = jsonType;
    }
    @JsonCreator
    public static SupportedAvroSchemaType fromJsonType(String jsonType) {
        for (SupportedAvroSchemaType type : values()) {
            if (type.jsonType.equals(jsonType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported schema type: " + jsonType);
    }
}