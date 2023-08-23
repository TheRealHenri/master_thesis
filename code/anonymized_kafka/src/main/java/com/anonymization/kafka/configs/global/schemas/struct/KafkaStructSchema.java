package com.anonymization.kafka.configs.global.schemas.struct;

import com.anonymization.kafka.configs.global.schemas.struct.StructSchemaField;

import java.util.List;

public class KafkaStructSchema {

    private String name;
    private List<StructSchemaField> fields;

    public KafkaStructSchema() {}

    public KafkaStructSchema(String name, List<StructSchemaField> fields) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StructSchemaField> getFields() {
        return fields;
    }

    public void setFields(List<StructSchemaField> fields) {
        this.fields = fields;
    }
}
