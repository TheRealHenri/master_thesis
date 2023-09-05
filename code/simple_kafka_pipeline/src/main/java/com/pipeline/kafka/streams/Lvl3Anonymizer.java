package com.pipeline.kafka.streams;

import com.pipeline.kafka.dataMasking.MaskingFunction;

public class Lvl3Anonymizer implements Anonymizer {
    MaskingFunction AddRelativeNoiseMF = mfCatalog.getByName("ADD_RELATIVE_NOISE");
    MaskingFunction BlurPhoneMF = mfCatalog.getByName("BLUR_PHONE");
    MaskingFunction BucketizeAge = mfCatalog.getByName("BUCKETIZE_NAME");
    MaskingFunction GeneralizeDiagnosis = mfCatalog.getByName("GENERALIZE_DIAGNOSIS");
    @Override
    public String anonymize(String value) {
        String result = "";
        // add moderate amount of relative noise
        // blur phone
        // bucketize age
        // generalize diagnosis
        return result;
    }
}
