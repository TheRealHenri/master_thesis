package com.dash.configs.stream;

import java.util.HashMap;
import java.util.List;

public class Parameter {

    private ParameterType type;
    private Object value;

    public Parameter() {}
    public Parameter(ParameterType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public List<String> getKeys() {
        return type == ParameterType.KEYS ? (List<String>) value : null;
    }

    public HashMap<String, String> getGeneralizationMap() { return type == ParameterType.GENERALIZATION_MAP ? (HashMap<String, String>) value : null; }

    public void setGeneralizationMap(HashMap<String, String> generalizationMap) {
        this.value = generalizationMap;
        this.type = ParameterType.GENERALIZATION_MAP;
    }

    public HashMap<String, Object> getConditionMap() { return type == ParameterType.CONDITION_MAP ? (HashMap<String, Object>) value : null; }

    public void setConditionMap(HashMap<String, Object> conditionMap) {
        this.value = conditionMap;
        this.type = ParameterType.CONDITION_MAP;
    }

    public List<String> getSubstitutionList() {
        return type == ParameterType.SUBSTITUTION_LIST ? (List<String>) value : null;
    }

    public void setSubstitutionList(List<String> substitutionList) {
        this.value = substitutionList;
        this.type = ParameterType.SUBSTITUTION_LIST;
    }

    public Integer getBucketSize() { return type == ParameterType.BUCKET_SIZE ? toInt() : null; }

    public void setBucketSize(Integer bucketSize) {
        this.value = bucketSize;
        this.type = ParameterType.BUCKET_SIZE;
    }

    public Integer getNFields() { return type == ParameterType.N_FIELDS ? toInt() : null; }

    public void setNFields(Integer nFields) {
        this.value = nFields;
        this.type = ParameterType.N_FIELDS;
    }

    public Integer getWindowSize() {
        return type == ParameterType.WINDOW_SIZE ? toInt() : null;
    }

    public void setWindowSize(Integer windowSize) {
        this.value = windowSize;
        this.type = ParameterType.WINDOW_SIZE;
    }

    public Integer getAdvanceTime() { return type == ParameterType.ADVANCE_TIME ? toInt() : null; }

    public void setAdvanceTime(Integer advanceTime) {
        this.value = advanceTime;
        this.type = ParameterType.ADVANCE_TIME;
    }

    public Integer getGracePeriod() { return type == ParameterType.GRACE_PERIOD ? toInt() : null; }

    public void setGracePeriod(Integer gracePeriod) {
        this.value = gracePeriod;
        this.type = ParameterType.GRACE_PERIOD;
    }

    public Integer getGroupSize() {
        return type == ParameterType.GROUP_SIZE ? toInt() : null;
    }

    public void setGroupSize(Integer groupSize) {
        this.value = groupSize;
        this.type = ParameterType.GROUP_SIZE;
    }

    public String getAggregationMode() { return type == ParameterType.AGGREGATION_MODE ? toString() : null; }

    public void setAggregationMode(String aggregationMode) {
        this.value = aggregationMode;
        this.type = ParameterType.AGGREGATION_MODE;
    }

    public Double getNoise() {
        return type == ParameterType.NOISE ? toDouble() : null;
    }

    public void setNoise(Double noise) {
        this.value = noise;
        this.type = ParameterType.NOISE;
    }

    public Long getSeed() { return type == ParameterType.SEED ? toLong() : null; }

    public void setSeed(Long seed) {
        this.value = seed;
        this.type = ParameterType.SEED;
    }

    public Boolean getShuffleIndividually() { return type == ParameterType.SHUFFLE_INDIVIDUALLY ? toBoolean() : null;}

    public Integer getK() {
        return type == ParameterType.K ? toInt() : null;
    }

    public void setK(Integer k) {
        this.value = k;
        this.type = ParameterType.K;
    }

    public Integer getMu() {
        return type == ParameterType.MU ? toInt() : null;
    }

    public Integer getDelta() {
        return type == ParameterType.DELTA ? toInt() : null;
    }

    public Integer getBeta() {
        return type == ParameterType.BETA ? toInt() : null;
    }

    public List<QuasiIdentifier> getQuasiIdentifiers() {
        return type == ParameterType.QUASI_IDENTIFIERS ? (List<QuasiIdentifier>) value : null;
    }

    public Integer getL() {
        return type == ParameterType.L ? toInt() : null;
    }

    public void setL(Integer l) {
        this.value = l;
        this.type = ParameterType.L;
    }

    public Integer getT() {
        return type == ParameterType.T ? toInt() : null;
    }

    public void setT(Integer t) {
        this.value = t;
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

    public Long toLong() {
        if (value instanceof Long) {
            return (Long) value;
        }
        throw new UnsupportedOperationException("Current parameter value is not a long");
    }

    public Boolean toBoolean() {
        if (value instanceof  Boolean) {
            return (Boolean) value;
        }
        throw new UnsupportedOperationException("Current parameter value is not a boolean");
    }
}