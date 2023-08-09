package com.pipeline.kafka.dataMasking.maskingFunctions;

import com.pipeline.kafka.dataMasking.MaskingFunction;

import java.util.Random;

public class AddRelativeNoise extends MaskingFunction {
    public AddRelativeNoise() {
        this.aggregable = true;
        this.name = "ADD_RELATIVE_NOISE";
    }

    public static int eval(double value, double relNoise) {
        assert (relNoise > 0.0 && relNoise < 1.0);
        double lowerBound = value - (value * relNoise);
        double upperBound = value + (value * relNoise);

        Random random = new Random();
        double randomValue = lowerBound + (upperBound - lowerBound) * random.nextDouble();

        return (int) Math.round(randomValue);
    }
}
