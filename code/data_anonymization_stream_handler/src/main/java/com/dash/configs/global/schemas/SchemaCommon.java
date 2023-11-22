package com.dash.configs.global.schemas;

import java.util.HashMap;


public class SchemaCommon {
    private final HashMap<String, FieldType> dataFields;

    public SchemaCommon(HashMap<String, FieldType> dataFields) {
        this.dataFields = dataFields;
    }

    public HashMap<String, FieldType> getDataFields() {
        return dataFields;
    }
}
