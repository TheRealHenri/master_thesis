package com.anonymization.kafka.anonymizers.valuebased;

import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.configs.stream.ParameterType;
import com.anonymization.kafka.validators.KeyValidator;
import com.anonymization.kafka.validators.ParameterExpectation;
import com.anonymization.kafka.validators.PositiveDoubleValidator;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NoiseMethods implements ValueBasedAnonymizer {

    private List<String> keysForNoise = Collections.emptyList();
    private Double noise;
    private final Logger log = LoggerFactory.getLogger(NoiseMethods.class);
    @Override
    public List<Struct> anonymize(List<Struct> lineS) {
        if (lineS.size() != 1) {
            log.info("Value based anonymizer {} called with more than one line", getClass().getName());
            return null;
        }

        Struct struct = lineS.get(0);
        Random random = new Random();

        for (Field field : struct.schema().fields()) {
            if (keysForNoise.contains(field.name())) {
                Schema schema = field.schema();
                if (schema.equals(Schema.OPTIONAL_INT32_SCHEMA) || schema.equals(Schema.INT32_SCHEMA)) {
                    int originalInt = Integer.parseInt(struct.get(field).toString());
                    struct.put(field.name(), addNoise(originalInt, random));
                } else if (schema.equals(Schema.OPTIONAL_INT64_SCHEMA) || schema.equals(Schema.INT64_SCHEMA)) {
                    long originalLong = Long.parseLong(struct.get(field).toString());
                    struct.put(field.name(), addNoise(originalLong, random));
                } else if (schema.equals(Schema.OPTIONAL_FLOAT32_SCHEMA) || schema.equals(Schema.FLOAT32_SCHEMA)) {
                    float originalFloat = Float.parseFloat(struct.get(field).toString());
                    struct.put(field.name(), addNoise(originalFloat, random));
                } else if (schema.equals(Schema.OPTIONAL_FLOAT64_SCHEMA) || schema.equals(Schema.FLOAT64_SCHEMA)) {
                    double originalDouble = Double.parseDouble(struct.get(field).toString());
                    struct.put(field.name(), addNoise(originalDouble, random));
                } else {
                    throw new IllegalArgumentException("Noise methods can only be applied to number types e.g. int, long, float or double. Does not work for provided Type: " + schema);
                }
            }
        }
        return List.of(struct);
    }

    private int addNoise(int originalValue, Random random) {
        return originalValue + (int) Math.round((2 * noise * originalValue) * (random.nextDouble() - 0.5));
    }

    private long addNoise(long originalValue, Random random) {
        return originalValue + Math.round((2 * noise * originalValue) * (random.nextDouble() - 0.5));
    }

    private float addNoise(float originalValue, Random random) {
        return originalValue + (float) ((2 * noise * originalValue) * (random.nextDouble() - 0.5));
    }

    private double addNoise(double originalValue, Random random) {
        return originalValue + (2 * noise * originalValue) * (random.nextDouble() - 0.5);
    }

    @Override
    public List<ParameterExpectation> getParameterExpectations() {
        return List.of(
                new ParameterExpectation(
                        ParameterType.KEYS.getName(),
                        List.of(new KeyValidator(true)),
                        true
                ),
                new ParameterExpectation(
                        ParameterType.NOISE.getName(),
                        List.of(new PositiveDoubleValidator()),
                        true
                )
        );
    }

    @Override
    public void initialize(List<Parameter> parameters) {
        for (Parameter parameter : parameters) {
            switch (parameter.getType()) {
                case KEYS:
                    this.keysForNoise = parameter.getKeys();
                    break;
                case NOISE:
                    this.noise = parameter.getNoise();
                    break;
            }
        }
    }

    public NoiseMethods() {
    }
}
