package com.anonymization.kafka.configs.stream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Parameter {

    private ParameterType type;
    private Object value;

    @JsonProperty("keys")
    private List<Key> getKeys() {
        return type == ParameterType.KEYS ? (List<Key>) value : null;
    }

    private void setKeys(List<Key> keys) {
        this.value = keys;
        this.type = ParameterType.KEYS;
    }

    @JsonProperty("windowSize")
    private String getWindowSize() {
        return type == ParameterType.WINDOW_SIZE ? value.toString() : null;
    }

    private void setWindowSize(String windowSize) {
        this.value = Integer.parseInt(windowSize);
        this.type = ParameterType.WINDOW_SIZE;
    }

    @JsonProperty("groupSize")
    private String getgroupSize() {
        return type == ParameterType.GROUP_SIZE ? value.toString() : null;
    }

    private void setGroupSize(String groupSize) {
        this.value = Integer.parseInt(groupSize);
        this.type = ParameterType.GROUP_SIZE;
    }

    @JsonProperty("noise")
    private String getNoise() {
        return type == ParameterType.NOISE ? value.toString() : null;
    }

    private void setNoise(String noise) {
        this.value = Double.parseDouble(noise);
        this.type = ParameterType.NOISE;
    }


    @JsonProperty("k")
    private String getK() {
        return type == ParameterType.K ? value.toString() : null;
    }

    private void setK(String k) {
        this.value = Integer.parseInt(k);
        this.type = ParameterType.K;
    }

    @JsonProperty("l")
    private String getL() {
        return type == ParameterType.L ? value.toString() : null;
    }

    private void setL(String l) {
        this.value = Integer.parseInt(l);
        this.type = ParameterType.L;
    }

    @JsonProperty("t")
    private String getT() {
        return type == ParameterType.T ? value.toString() : null;
    }

    private void setT(String t) {
        this.value = Integer.parseInt(t);
        this.type = ParameterType.T;
    }

    public ParameterType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public String toString() {
        return value.toString();
    }

    public Integer toInt() {
        if (value instanceof Integer) {
            return (Integer) value;
        }
        throw new UnsupportedOperationException("Current parameter value is not an integer.");
    }

    public Double toDouble() {
        if (value instanceof Double) {
            return (Double) value;
        }
        throw new UnsupportedOperationException("Current parameter value is not a double.");
    }

    @JsonValue
    public Map<String, Object> toJsonValue() {
        Map<String, Object> map = new HashMap<>();
        switch (type) {
            case KEYS:
                map.put("keys", value);
                break;
            case WINDOW_SIZE:
                map.put("windowSize", value.toString());
                break;
            case GROUP_SIZE:
                map.put("groupSize", value.toString());
                break;
            case NOISE:
                map.put("noise", value.toString());
                break;
            case K:
                map.put("k", value.toString());
                break;
            case L:
                map.put("l", value.toString());
                break;
            case T:
                map.put("t", value.toString());
                break;
        }
        return map;
    }
}