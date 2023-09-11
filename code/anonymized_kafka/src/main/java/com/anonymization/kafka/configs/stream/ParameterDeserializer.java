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
            case GENERALIZATION_MAP:
                HashMap<String, String> generalizationMap = new HashMap<>();
                JsonNode generalizationMapNode = jsonNode.get("generalizationMap");
                Iterator<Map.Entry<String, JsonNode>> generalizationMapIterator = generalizationMapNode.fields();
                while (generalizationMapIterator.hasNext()) {
                    Map.Entry<String, JsonNode> entry = generalizationMapIterator.next();
                    generalizationMap.put(entry.getKey(), entry.getValue().asText());
                }
                return generalizationMap;
            case CONDITION_MAP:
                HashMap<String, Object> conditionMap = new HashMap<>();
                JsonNode conditionMapNode = jsonNode.get("conditionMap");
                Iterator<Map.Entry<String, JsonNode>> conditionMapIterator = conditionMapNode.fields();
                while (conditionMapIterator.hasNext()) {
                    Map.Entry<String, JsonNode> entry = conditionMapIterator.next();
                    if (entry.getValue().isValueNode()) {
                        conditionMap.put(entry.getKey(), entry.getValue().asText());
                    } else {
                        List<Number> range = new ArrayList<>();
                        Iterator<JsonNode> rangeIterator = entry.getValue().elements();
                        rangeIterator.forEachRemaining(number -> range.add(number.numberValue()));
                        conditionMap.put(entry.getKey(), range);
                    }
                }
                return conditionMap;
            case SUBSTITUTION_LIST:
                List<String> substitutionList = new ArrayList<>();
                JsonNode subNode = jsonNode.get("substitutionList");
                Iterator<JsonNode> subIterator = subNode.elements();
                while (subIterator.hasNext()) {
                    substitutionList.add(subIterator.next().asText());
                }
                return substitutionList;
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