package com.pipeline.kafka.streams;

import com.pipeline.kafka.dataMasking.MaskingFunction;

public class Lvl2Anonymizer implements Anonymizer {
    MaskingFunction AddRelativeNoiseMF = mfCatalog.getByName("ADD_RELATIVE_NOISE");
    MaskingFunction BlurPhoneMF = mfCatalog.getByName("BLUR_PHONE");
    MaskingFunction GeneralizeDiagnosisMF = mfCatalog.getByName("GENERALIZE_DIAGNOSIS");
    @Override
    public String anonymize(String value) {
        String result = "";
        // add some relative noise
        // blur phone
        // generalize diagnosis
        return result;
    }
}
