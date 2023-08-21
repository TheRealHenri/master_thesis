package com.pipeline.kafka.streams;

import com.pipeline.kafka.dataMasking.MaskingFunction;

public class Lvl1Anonymizer implements Anonymizer {
    MaskingFunction AddRelativeNoiseMF = mfCatalog.getByName("ADD_RELATIVE_NOISE");
    MaskingFunction GeneralizeDiagnosisMF = mfCatalog.getByName("GENERALIZE_DIAGNOSIS");
    @Override
    public String anonymize(String value) {
        String result = "";
        // add little relative noise
        // generalize diagnosis
        return result;
    }
}
