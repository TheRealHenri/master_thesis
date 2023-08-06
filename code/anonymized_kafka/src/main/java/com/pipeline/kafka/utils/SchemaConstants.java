package com.pipeline.kafka.utils;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;

public class SchemaConstants {

    public static final Schema SYNTHETIC_DATA_CSV_SCHEMA = SchemaBuilder.struct()
            .name("com.pipeline.kafka.connectors.SyntheticData")
            .field("id", Schema.INT32_SCHEMA)
            .field("name", Schema.STRING_SCHEMA)
            .field("gender", Schema.STRING_SCHEMA)
            .field("age", Schema.INT32_SCHEMA)
            .field("height", Schema.INT32_SCHEMA)
            .field("weight", Schema.INT32_SCHEMA)
            .field("diagnosis", Schema.STRING_SCHEMA)
            .field("address", Schema.STRING_SCHEMA)
            .field("zip", Schema.INT32_SCHEMA)
            .field("phone", Schema.STRING_SCHEMA)
            .build();

    private SchemaConstants() {
        throw new AssertionError("This class should not be instantiated.");
    }
}
