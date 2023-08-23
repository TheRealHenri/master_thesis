package com.anonymization.kafka.configs.global.schemas.avro;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.List;

public class AvroTypeDeserializer extends JsonDeserializer<AvroType> {
    @Override
    public AvroType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        AvroType avroType = new AvroType();

        JsonToken currentToken = p.getCurrentToken();
        if (currentToken == JsonToken.START_ARRAY) {
            // Parse as a list of SupportedAvroSchemaType (union type)
            List<SupportedAvroSchemaType> unionTypes = p.readValueAs(new TypeReference<List<SupportedAvroSchemaType>>() {});
            avroType.setUnionTypes(unionTypes);
        } else if (currentToken == JsonToken.VALUE_STRING) {
            // Parse as a simple SupportedAvroSchemaType
            avroType.setSimpleType(SupportedAvroSchemaType.fromJsonType(p.getValueAsString()));
        } else {
            throw new IOException("Unexpected JSON token for AvroType");
        }

        return avroType;
    }
}
