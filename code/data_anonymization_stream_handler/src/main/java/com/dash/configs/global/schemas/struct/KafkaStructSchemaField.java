package com.dash.configs.global.schemas.struct;


import com.dash.configs.global.schemas.FieldType;

import java.util.Map;

public class KafkaStructSchemaField {
    private String name;
    private KafkaStructType type;

    public KafkaStructSchemaField() {}

    public KafkaStructSchemaField(String name, KafkaStructType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public KafkaStructType getType() {
        return type;
    }

    public void setType(KafkaStructType type) {
        this.type = type;
    }

    public Map<String, FieldType> getSchemaField() {
        return Map.of(name, type.getFieldType());
    }
}
