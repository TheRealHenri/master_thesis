package com.anonymization.kafka.configs.global.schemas.struct;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.connect.data.Schema;

public enum SupportedStructSchemaType {
    INT32("int32", Schema.INT32_SCHEMA),
    INT64("int64", Schema.INT64_SCHEMA),
    STRING("string", Schema.STRING_SCHEMA),
    Bytes("bytes", Schema.BYTES_SCHEMA),
    FLOAT32("float32", Schema.FLOAT32_SCHEMA),
    FLOAT64("float64", Schema.FLOAT64_SCHEMA),
    BOOLEAN("boolean", Schema.BOOLEAN_SCHEMA);
    private final String jsonType;
    private final Schema kafkaSchema;

    SupportedStructSchemaType(String jsonType, Schema kafkaSchema) {
        this.jsonType = jsonType;
        this.kafkaSchema = kafkaSchema;
    }
    @JsonCreator
    public static SupportedStructSchemaType fromJsonType(String jsonType) {
        for (SupportedStructSchemaType type : values()) {
            if (type.jsonType.equals(jsonType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported schema type: " + jsonType);
    }

    public Schema toKafkaSchema() {
        return kafkaSchema;
    }
}
