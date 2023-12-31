package com.pipeline.kafka.dataMasking.maskingFunctions;

import com.pipeline.kafka.dataMasking.MaskingFunction;

public class BlurZip extends MaskingFunction {

    public BlurZip() {
        this.name = "BLUR_ZIP";
    }

    public static String eval(String zipCode, int nFields) {
        assert (nFields <= 6 && nFields > 0);
        return zipCode.substring(0, zipCode.length() - nFields) + "X".repeat(nFields);
    }
}
