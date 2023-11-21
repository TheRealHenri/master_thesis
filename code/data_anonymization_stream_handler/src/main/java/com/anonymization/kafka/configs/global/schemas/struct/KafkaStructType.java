package com.anonymization.kafka.configs.global.schemas.struct;

import com.anonymization.kafka.configs.global.schemas.FieldType;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.kafka.connect.data.Schema;

public enum KafkaStructType {
    INT32("int32", Schema.INT32_SCHEMA, FieldType.INT),
    OPTIONAL_INT32("optional_int32", Schema.OPTIONAL_INT32_SCHEMA, FieldType.OPTIONAL_INT),
    INT64("int64", Schema.INT64_SCHEMA, FieldType.LONG),
    OPTIONAL_INT64("optional_int64", Schema.OPTIONAL_INT64_SCHEMA, FieldType.OPTIONAL_LONG),
    STRING("string", Schema.STRING_SCHEMA, FieldType.STRING),
    OPTIONAL_STRING("optional_string", Schema.OPTIONAL_STRING_SCHEMA, FieldType.OPTIONAL_STRING),
    FLOAT32("float32", Schema.FLOAT32_SCHEMA, FieldType.FLOAT),
    OPTIONAL_FLOAT32("optional_float32", Schema.OPTIONAL_FLOAT32_SCHEMA, FieldType.OPTIONAL_FLOAT),
    FLOAT64("float64", Schema.FLOAT64_SCHEMA, FieldType.DOUBLE),
    OPTIONAL_FLOAT64("optional_float64", Schema.OPTIONAL_FLOAT64_SCHEMA, FieldType.OPTIONAL_DOUBLE),
    BOOLEAN("boolean", Schema.BOOLEAN_SCHEMA, FieldType.BOOLEAN),
    OPTIONAL_BOOLEAN("optional_boolean", Schema.OPTIONAL_BOOLEAN_SCHEMA, FieldType.OPTIONAL_BOOLEAN);

    private final String jsonType;
    private final Schema kafkaSchema;
    private final FieldType fieldType;

    KafkaStructType(String jsonType, Schema kafkaSchema, FieldType fieldType) {
        this.jsonType = jsonType;
        this.kafkaSchema = kafkaSchema;
        this.fieldType = fieldType;
    }
    @JsonCreator
    public static KafkaStructType fromJsonType(String jsonType) {
        for (KafkaStructType type : values()) {
            if (type.jsonType.equals(jsonType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported schema type: " + jsonType);
    }

    public Schema toKafkaSchema() {
        return kafkaSchema;
    }

    public FieldType getFieldType() {
        return fieldType;
    }
}
