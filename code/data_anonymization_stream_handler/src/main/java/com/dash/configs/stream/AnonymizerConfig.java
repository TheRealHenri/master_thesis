package com.dash.configs.stream;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

public class AnonymizerConfig {
    private String anonymizer;
    @JsonDeserialize(using = ParameterDeserializer.class)
    private List<Parameter> parameters;

    public AnonymizerConfig(){}
    public AnonymizerConfig(String anonymizer, List<Parameter> parameters) {
        this.anonymizer = anonymizer;
        this.parameters = parameters;
    }

    public String getAnonymizer() {
        return anonymizer;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setAnonymizer(String anonymizer) {
        this.anonymizer = anonymizer;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }
}

