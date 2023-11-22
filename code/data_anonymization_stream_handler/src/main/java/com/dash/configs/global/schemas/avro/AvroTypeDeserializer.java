package com.dash.configs.global.schemas.avro;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AvroTypeDeserializer extends JsonDeserializer<AvroType> {
    @Override
    public AvroType deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        if (node.isArray()) {
            List<String> types = new ArrayList<>();
            for (JsonNode n : node) {
                types.add(n.asText());
            }
            return AvroType.fromUnionOrType(types);
        } else {
            return AvroType.fromSingleType(node.asText());
        }
    }
}
