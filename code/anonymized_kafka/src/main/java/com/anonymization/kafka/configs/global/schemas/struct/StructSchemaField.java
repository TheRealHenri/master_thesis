package com.anonymization.kafka.configs.global.schemas.struct;

import com.anonymization.kafka.configs.global.schemas.avro.SupportedAvroSchemaType;

public class StructSchemaField {
    private String name;
    private SupportedStructSchemaType type;

    public StructSchemaField() {}

    public StructSchemaField(String name, SupportedStructSchemaType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SupportedStructSchemaType getType() {
        return type;
    }

    public void setType(SupportedStructSchemaType type) {
        this.type = type;
    }
}
