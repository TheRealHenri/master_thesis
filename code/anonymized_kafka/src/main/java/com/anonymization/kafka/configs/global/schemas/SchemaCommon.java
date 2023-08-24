package com.anonymization.kafka.configs.global.schemas;

import com.anonymization.kafka.configs.global.schemas.avro.AvroSchema;
import com.anonymization.kafka.configs.global.schemas.struct.KafkaStructSchema;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;
import java.util.Map;


public class SchemaCommon {
    private final List<Map<String, FieldType>> dataFields;

    public SchemaCommon(List<Map<String, FieldType>> dataFields) {
        this.dataFields = dataFields;
    }

    public List<Map<String, FieldType>> getDataFields() {
        return dataFields;
    }
}
