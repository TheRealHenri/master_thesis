package com.anonymization.kafka.configs.stream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.*;

public class ParameterDeserializer extends JsonDeserializer<List<Parameter>> {
    @Override
    public List<Parameter> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        List<Parameter> parametersList = new ArrayList<>();

        for (JsonNode parameterSetNode : node) {
            for (ParameterType type : ParameterType.values()) {
                if (parameterSetNode.has(type.getName())) {
                    parametersList.add(new Parameter(type, parseValue(type, parameterSetNode)));
                }
            }
        }
        return parametersList;
    }

    private Object parseValue(ParameterType type, JsonNode jsonNode) {
        switch (type) {
            case KEYS:
                List<String> keys = new ArrayList<>();
                JsonNode keyNode = jsonNode.get("keys");
                Iterator<JsonNode> keyIterator = keyNode.elements();
                while (keyIterator.hasNext()) {
                    keys.add(keyIterator.next().asText());
                }
                return keys;
            case MAP:
                HashMap<String, String> map = new HashMap<>();
                JsonNode mapNode = jsonNode.get("map");
                Iterator<Map.Entry<String, JsonNode>> mapIterator = mapNode.fields();
                while (mapIterator.hasNext()) {
                    Map.Entry<String, JsonNode> entry = mapIterator.next();
                    map.put(entry.getKey(), entry.getValue().asText());
                }
                return map;
            case BUCKET_SIZE:
                return jsonNode.get("bucketSize").asInt();
            case N_FIELDS:
                return jsonNode.get("nFields").asInt();
            case WINDOW_SIZE:
                return jsonNode.get("windowSize").asInt();
            case GROUP_SIZE:
                return jsonNode.get("groupSize").asInt();
            case K:
                return jsonNode.get("k").asInt();
            case L:
                return jsonNode.get("l").asInt();
            case T:
                return jsonNode.get("t").asInt();
            case NOISE:
                return jsonNode.get("noise").asDouble();
            default:
                throw new RuntimeException("Parameter type " + type + " not supported.");
        }
    }
}