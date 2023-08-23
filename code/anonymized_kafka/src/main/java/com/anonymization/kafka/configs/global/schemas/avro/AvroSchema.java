package com.anonymization.kafka.configs.global.schemas.avro;

import com.anonymization.kafka.configs.global.schemas.struct.StructSchemaField;

import java.util.List;

public class AvroSchema {

    private String namespace;
    private String type;
    private String name;
    private List<AvroDataSchemaField> fields;

    public AvroSchema() {}

    public AvroSchema(String namespace, String type, String name, List<AvroDataSchemaField> fields) {
        this.namespace = namespace;
        this.type = type;
        this.name = name;
        this.fields = fields;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AvroDataSchemaField> getFields() {
        return fields;
    }

    public void setFields(List<AvroDataSchemaField> fields) {
        this.fields = fields;
    }
}
