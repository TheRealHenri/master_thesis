package com.anonymization.kafka.configs.global.schemas.avro;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;


@JsonDeserialize(using = AvroTypeDeserializer.class)
public class AvroType {
    private SupportedAvroSchemaType simpleType;
    private List<SupportedAvroSchemaType> unionTypes;

    public AvroType() {}

    public AvroType(SupportedAvroSchemaType simpleType, List<SupportedAvroSchemaType> unionTypes) {
        this.simpleType = simpleType;
        this.unionTypes = unionTypes;
    }

    public SupportedAvroSchemaType getSimpleType() {
        return simpleType;
    }

    public void setSimpleType(SupportedAvroSchemaType simpleType) {
        this.simpleType = simpleType;
    }

    public List<SupportedAvroSchemaType> getUnionTypes() {
        return unionTypes;
    }

    public void setUnionTypes(List<SupportedAvroSchemaType> unionTypes) {
        this.unionTypes = unionTypes;
    }
}
