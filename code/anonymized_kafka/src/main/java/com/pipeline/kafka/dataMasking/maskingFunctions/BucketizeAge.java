package com.pipeline.kafka.dataMasking.maskingFunctions;

import com.pipeline.kafka.dataMasking.MaskingFunction;

public class BucketizeAge extends MaskingFunction {

    public BucketizeAge() {
        this.name = "BUCKETIZE_AGE";
    }

    public static String eval(int age, int bSize) {
        int l = age / bSize;
        int h = l + 1;
        return "[" + (l * bSize) + " - " + ((h * bSize) - 1) + "]";
    }
}

