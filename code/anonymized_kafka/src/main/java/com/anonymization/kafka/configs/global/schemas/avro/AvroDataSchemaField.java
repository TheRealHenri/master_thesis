package com.anonymization.kafka.configs.global.schemas.avro;

public class AvroDataSchemaField {
    private String name;
    private AvroType type;

    public AvroDataSchemaField() {}

    public AvroDataSchemaField(String name, AvroType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AvroType getType() {
        return type;
    }

    public void setType(AvroType type) {
        this.type = type;
    }
}
