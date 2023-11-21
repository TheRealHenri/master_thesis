package com.anonymization.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AnonymizationCategory {
    VALUE_BASED("VALUE_BASED"),
    TUPLE_BASED("TUPLE_BASED"),
    ATTRIBUTE_BASED("ATTRIBUTE_BASED"),
    TABLE_BASED("TABLE_BASED");

    private final String name;

    AnonymizationCategory(String name) {
        this.name = name;
    }

    @JsonCreator
    public static AnonymizationCategory fromString(String name) {
        return AnonymizationCategory.valueOf(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
