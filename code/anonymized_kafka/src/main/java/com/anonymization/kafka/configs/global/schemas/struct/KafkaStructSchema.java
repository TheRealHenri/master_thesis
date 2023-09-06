package com.anonymization.kafka.configs.global.schemas.struct;

import com.anonymization.kafka.configs.global.schemas.DataSchema;
import com.anonymization.kafka.configs.global.schemas.FieldType;
import com.anonymization.kafka.configs.global.schemas.SchemaCommon;
import com.anonymization.kafka.configs.global.schemas.SchemaType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;

import java.util.HashMap;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class KafkaStructSchema implements DataSchema {
    private String name;
    private List<KafkaStructSchemaField> fields;

    public KafkaStructSchema() {}

    public KafkaStructSchema(String name, List<KafkaStructSchemaField> fields) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<KafkaStructSchemaField> getFields() {
        return fields;
    }

    public void setFields(List<KafkaStructSchemaField> fields) {
        this.fields = fields;
    }

    @Override
    public SchemaType getSchemaType() {
        return SchemaType.KAFKA_STRUCT;
    }

    @Override
    public SchemaCommon getSchema() {
        HashMap<String, FieldType> dataFields = new HashMap<>();
        for (KafkaStructSchemaField field : fields) {
            dataFields.putAll(field.getSchemaField());
        }
        return new SchemaCommon(dataFields);
    }

    @Override
    public Schema getKafkaSchema() {
        SchemaBuilder schemaBuilder = SchemaBuilder.struct().name(name);

        for (KafkaStructSchemaField field : fields) {
            schemaBuilder.field(field.getName(), field.getType().toKafkaSchema());
        }
        return schemaBuilder.build();
    }


}
