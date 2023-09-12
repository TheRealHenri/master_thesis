package com.anonymization.kafka.anonymizers.window;

import com.anonymization.kafka.validators.ParsableEnum;

public enum WindowType implements ParsableEnum {
    TUMBLING("tumbling"),
    SLIDING("sliding");


    private final String name;

    WindowType(String name) {
        this.name = name;
    }

    public static WindowType getByName(String name) {
        for (WindowType type : WindowType.values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Enum WindowType does not support " + name);
    }

    @Override
    public String getName() {
        return name;
    }
}
