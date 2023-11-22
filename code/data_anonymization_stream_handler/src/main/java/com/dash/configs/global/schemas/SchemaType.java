package com.dash.configs.global.schemas;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SchemaType {

    AVRO("AVRO"),
    KAFKA_STRUCT("KAFKA_STRUCT");

    private final String name;

    SchemaType(String name) {
        this.name = name;
    }

    @JsonCreator
    public static SchemaType fromString(String name) {
        return SchemaType.valueOf(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
