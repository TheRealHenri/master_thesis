package com.dash.configs.global.schemas;

import org.apache.kafka.connect.data.Schema;

import java.util.Optional;

public enum FieldType {
    STRING(String.class, false),
    OPTIONAL_STRING(String.class, true),
    INT(Integer.class, false),
    OPTIONAL_INT(Integer.class, true),
    LONG(Long.class, false),
    OPTIONAL_LONG(Long.class, true),
    FLOAT(Float.class, false),
    OPTIONAL_FLOAT(Float.class, true),
    DOUBLE(Double.class, false),
    OPTIONAL_DOUBLE(Double.class, true),
    BOOLEAN(Boolean.class, false),
    OPTIONAL_BOOLEAN(Boolean.class, true);

    private final Class<?> javaType;
    private final boolean nullable;

    FieldType(Class<?> javaType, boolean nullable) {
        this.javaType = javaType;
        this.nullable = nullable;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public boolean isNullable() {
        return nullable;
    }
}