package com.pipeline.kafka.utils;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

public class SchemaConstants {

    public static final Schema SYNTHETIC_DATA_CSV_SCHEMA = SchemaBuilder.struct()
            .name("com.pipeline.kafka.connectors.SyntheticData")
            .field("id", Schema.INT32_SCHEMA)
            .field("name", Schema.STRING_SCHEMA)
            .field("address", Schema.STRING_SCHEMA)
            .field("zip", Schema.INT32_SCHEMA)
            .field("phone", Schema.STRING_SCHEMA)
            .field("gender", Schema.STRING_SCHEMA)
            .field("height", Schema.INT32_SCHEMA)
            .field("weight", Schema.INT32_SCHEMA)
            .field("age", Schema.INT32_SCHEMA)
            .field("insurance_company", Schema.INT32_SCHEMA)
            .field("insurance_number", Schema.STRING_SCHEMA)
            .field("diagnosis", Schema.STRING_SCHEMA)
            .field("glucose", Schema.INT32_SCHEMA)
            .field("HbA1C", Schema.FLOAT64_SCHEMA)
            .field("medication", Schema.STRING_SCHEMA)
            .build();

    public static Struct SYNTHETIC_DATA_CSV_TO_STRUCT_FOR (String line) {
        Struct resultingStruct = new Struct(SYNTHETIC_DATA_CSV_SCHEMA);
        String[] values = line.split(",");
        resultingStruct.put("id", Integer.parseInt(values[0]));
        resultingStruct.put("name", values[1]);
        resultingStruct.put("address", values[2]);
        resultingStruct.put("zip", Integer.parseInt(values[3]));
        resultingStruct.put("phone", values[4]);
        resultingStruct.put("gender", values[5]);
        resultingStruct.put("height", Integer.parseInt(values[6]));
        resultingStruct.put("weight", Integer.parseInt(values[7]));
        resultingStruct.put("age", Integer.parseInt(values[8]));
        resultingStruct.put("insurance_company", Integer.parseInt(values[9]));
        resultingStruct.put("insurance_number", values[10]);
        resultingStruct.put("diagnosis", values[11]);
        resultingStruct.put("glucose", Integer.parseInt(values[12]));
        resultingStruct.put("HbA1C", Double.parseDouble(values[13]));
        resultingStruct.put("medication", values[14]);
        return resultingStruct;
    }

    private SchemaConstants() {
        throw new AssertionError("This class should not be instantiated.");
    }
}
