package com.anonymization.kafka.configs.stream;

import com.anonymization.kafka.anonymizers.tablebased.datastructures.CategoricalHierarchy;
import com.anonymization.kafka.anonymizers.tablebased.datastructures.GeneralizationHierarchy;
import com.anonymization.kafka.anonymizers.tablebased.datastructures.NumericalHierarchy;
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
        JsonNode valueNode = jsonNode.get(type.getName());
        switch (type) {
            case KEYS:
                List<String> keys = new ArrayList<>();
                Iterator<JsonNode> keyIterator = valueNode.elements();
                while (keyIterator.hasNext()) {
                    keys.add(keyIterator.next().asText());
                }
                return keys;
            case GENERALIZATION_MAP:
                HashMap<String, String> generalizationMap = new HashMap<>();
                Iterator<Map.Entry<String, JsonNode>> generalizationMapIterator = valueNode.fields();
                while (generalizationMapIterator.hasNext()) {
                    Map.Entry<String, JsonNode> entry = generalizationMapIterator.next();
                    generalizationMap.put(entry.getKey(), entry.getValue().asText());
                }
                return generalizationMap;
            case CONDITION_MAP:
                HashMap<String, Object> conditionMap = new HashMap<>();
                Iterator<Map.Entry<String, JsonNode>> conditionMapIterator = valueNode.fields();
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
            case QUASI_IDENTIFIERS:
                List<QuasiIdentifier> quasiIdentifiers = new ArrayList<>();
                Iterator<JsonNode> quasiIdentifiersIterator = valueNode.elements();
                while (quasiIdentifiersIterator.hasNext()) {
                    JsonNode quasiIdentifierNode = quasiIdentifiersIterator.next();
                    Iterator<JsonNode> quasiIdentifierIterator = quasiIdentifierNode.elements();
                    String key = quasiIdentifierIterator.next().asText();
                    GeneralizationHierarchy hierarchy;
                    JsonNode hierarchyNode = quasiIdentifierIterator.next();
                    if (quasiIdentifierNode.has("bucketing")) {
                        hierarchy = getNumericalHierarchyFor(hierarchyNode);
                    } else if (quasiIdentifierNode.has("hierarchy")) {
                        hierarchy = getCategoricalHierarchyFor(hierarchyNode);
                    } else {
                        throw new RuntimeException("Hierarchy type " + hierarchyNode.asText() + " not supported.");
                    }
                    quasiIdentifiers.add(new QuasiIdentifier(key, hierarchy));
                }
                return quasiIdentifiers;
            case SUBSTITUTION_LIST:
                List<String> substitutionList = new ArrayList<>();
                Iterator<JsonNode> subIterator = valueNode.elements();
                while (subIterator.hasNext()) {
                    substitutionList.add(subIterator.next().asText());
                }
                return substitutionList;
            case BUCKET_SIZE:
            case N_FIELDS:
            case WINDOW_SIZE:
            case ADVANCE_TIME:
            case GRACE_PERIOD:
            case GROUP_SIZE:
            case K:
            case DELTA:
            case MU:
            case BETA:
            case L:
            case T:
                return valueNode.asInt();
            case AGGREGATION_MODE:
                return valueNode.asText();
            case SEED:
                return valueNode.asLong();
            case SHUFFLE_INDIVIDUALLY:
                return valueNode.asBoolean();
            case NOISE:
                return valueNode.asDouble();
            default:
                throw new RuntimeException("Parameter type " + type + " not supported.");
        }
    }

    private NumericalHierarchy getNumericalHierarchyFor(JsonNode hierarchyNode) {
        NumericalHierarchy hierarchy = new NumericalHierarchy(0,0,0);
        Iterator<Map.Entry<String, JsonNode>> conditionMapIterator = hierarchyNode.fields();
        while (conditionMapIterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = conditionMapIterator.next();
            if (entry.getValue().isValueNode()) {
                hierarchy.setBucketSize(entry.getValue().asInt());
            } else {
                Iterator<JsonNode> rangeIterator = entry.getValue().elements();
                hierarchy.setRangeStart(rangeIterator.next().asInt());
                hierarchy.setRangeEnd(rangeIterator.next().asInt());
            }
        }
        return hierarchy;
    }
    private CategoricalHierarchy getCategoricalHierarchyFor(JsonNode hierarchyNode) {
        if (hierarchyNode.isEmpty()) {
            return null;
        } else {
            String value = hierarchyNode.get("value").asText();
            List<CategoricalHierarchy> children = new ArrayList<>(Collections.emptyList());
            if (hierarchyNode.has("children")) {
                JsonNode childrenNode = hierarchyNode.get("children");
                Iterator<JsonNode> childrenIterator = childrenNode.elements();
                while (childrenIterator.hasNext()) {
                    children.add(getCategoricalHierarchyFor(childrenIterator.next()));
                }
            }
            return new CategoricalHierarchy(value, children);
        }
    }
}