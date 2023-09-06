package com.anonymization.kafka.configs.global.schemas.avro;

import com.anonymization.kafka.configs.global.schemas.DataSchema;
import com.anonymization.kafka.configs.global.schemas.FieldType;
import com.anonymization.kafka.configs.global.schemas.SchemaCommon;
import com.anonymization.kafka.configs.global.schemas.SchemaType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.kafka.connect.data.Schema;

import java.util.HashMap;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AvroSchema implements DataSchema {
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

    @Override
    public SchemaType getSchemaType() {
        return SchemaType.AVRO;
    }

    @Override
    public SchemaCommon getSchema() {
        HashMap<String, FieldType> dataFields = new HashMap<>();
        for (AvroDataSchemaField field : fields) {
            dataFields.putAll(field.getSchemaField());
        }
        return new SchemaCommon(dataFields);
    }

    @Override
    public Schema getKafkaSchema() {
        return null;
    }
}
