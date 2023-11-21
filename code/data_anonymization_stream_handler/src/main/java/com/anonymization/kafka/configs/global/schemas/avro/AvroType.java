package com.anonymization.kafka.configs.global.schemas.avro;

import com.anonymization.kafka.configs.global.schemas.FieldType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;
import java.util.stream.Collectors;


@JsonDeserialize(using = AvroTypeDeserializer.class)
public enum AvroType {
    BOOLEAN("boolean",FieldType.BOOLEAN),
    OPTIONAL_BOOLEAN("optional_boolean", FieldType.OPTIONAL_BOOLEAN),
    INT("int", FieldType.INT),
    OPTIONAL_INT("optional_int", FieldType.OPTIONAL_INT),
    LONG("long", FieldType.LONG),
    OPTIONAL_LONG("optional_long", FieldType.OPTIONAL_LONG),
    FLOAT("float", FieldType.FLOAT),
    OPTIONAL_FLOAT("optional_float", FieldType.OPTIONAL_FLOAT),
    DOUBLE("double", FieldType.DOUBLE),
    OPTIONAL_DOUBLE("optional_double", FieldType.OPTIONAL_DOUBLE),
    STRING("string", FieldType.STRING),
    OPTIONAL_STRING("optional_string", FieldType.OPTIONAL_STRING),
    NULL("null", null);

    private final String jsonType;
    private final FieldType fieldType;

    AvroType(String jsonType, FieldType fieldType) {
        this.jsonType = jsonType;
        this.fieldType = fieldType;
    }

    @JsonCreator
    public static AvroType fromUnionOrType(List<String> unionOrType) {
        if (unionOrType.size() == 1) {
            String type = unionOrType.get(0);
            for (AvroType avroType : values()) {
                if (avroType.jsonType.equals(type)) {
                    return avroType;
                }
            }
            throw new IllegalArgumentException("Unsupported schema type: " + type);
        } else {
            List<AvroType> unionTypes = unionOrType.stream()
                    .map(AvroType::fromSingleType)
                    .collect(Collectors.toList());
            if (unionTypes.contains(NULL)) {
                return unionTypes.stream().filter(t -> t != NULL).findFirst().orElseThrow(IllegalArgumentException::new).toOptionalType();
            }
            throw new IllegalArgumentException("Unsupported union type: " + unionOrType.toString());
        }
    }

    public static AvroType fromSingleType(String type) {
        for (AvroType avroType : values()) {
            if (avroType.jsonType.equals(type)) {
                return avroType;
            }
        }
        throw new IllegalArgumentException("Unsupported schema type: " + type);
    }

    public AvroType toOptionalType() {
        switch (this) {
            case BOOLEAN:
                return OPTIONAL_BOOLEAN;
            case INT:
                return OPTIONAL_INT;
            case LONG:
                return OPTIONAL_LONG;
            case FLOAT:
                return OPTIONAL_FLOAT;
            case DOUBLE:
                return OPTIONAL_DOUBLE;
            case STRING:
                return OPTIONAL_STRING;
            default:
                throw new IllegalArgumentException("No nullable counterpart for: " + this.name());
        }
    }

    public FieldType getFieldType() {
        return fieldType;
    }
}