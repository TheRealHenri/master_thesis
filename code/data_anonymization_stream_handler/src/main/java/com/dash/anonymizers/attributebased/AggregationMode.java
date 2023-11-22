package com.dash.anonymizers.attributebased;

import com.dash.validators.ParsableEnum;

public enum AggregationMode implements ParsableEnum {
    SUM("sum"),
    MEDIAN("median"),
    AVERAGE("average"),
    MAX("max"),
    MIN("min"),
    COUNT("count"),
    MODE("mode");

    private String name;

    AggregationMode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public static AggregationMode getByName(String name) {
        for (AggregationMode type : AggregationMode.values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Enum WindowType does not support " + name);
    }
}
