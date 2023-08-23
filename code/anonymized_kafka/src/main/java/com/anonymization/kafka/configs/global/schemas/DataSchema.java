package com.anonymization.kafka.configs.global.schemas;

import com.anonymization.kafka.configs.global.schemas.avro.AvroSchema;
import com.anonymization.kafka.configs.global.schemas.struct.KafkaStructSchema;

public class DataSchema {

    private String schemaType;
    private KafkaStructSchema kafkaStructSchema;

    private AvroSchema avroSchema;

    public DataSchema() {}

    public DataSchema(String schemaType, KafkaStructSchema kafkaStructSchema, AvroSchema avroSchema) {
        this.schemaType = schemaType;
        this.kafkaStructSchema = kafkaStructSchema;
        this.avroSchema = avroSchema;
    }

    public DataSchema(String schemaType, KafkaStructSchema kafkaStructSchema) {
        this.schemaType = schemaType;
        this.kafkaStructSchema = kafkaStructSchema;
    }

    public DataSchema(String schemaType, AvroSchema avroSchema) {
        this.schemaType = schemaType;
        this.avroSchema = avroSchema;
    }

    public boolean isAvro() {
        return "avro".equalsIgnoreCase(schemaType);
    }

    public boolean isKafkaStruct() {
        return "kafkaStruct".equalsIgnoreCase(schemaType);
    }

    public String getSchemaType() {
        return schemaType;
    }

    public void setSchemaType(String schemaType) {
        this.schemaType = schemaType;
    }

    public KafkaStructSchema getKafkaStructSchema() {
        return kafkaStructSchema;
    }

    public void setKafkaStructSchema(KafkaStructSchema kafkaStructSchema) {
        this.kafkaStructSchema = kafkaStructSchema;
    }

    public AvroSchema getAvroSchema() {
        return avroSchema;
    }

    public void setAvroSchema(AvroSchema avroSchema) {
        this.avroSchema = avroSchema;
    }
}
