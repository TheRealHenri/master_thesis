package com.pipeline.kafka.streams;

import com.pipeline.kafka.dataMasking.MaskingFunction;

public class Lvl4Anonymizer implements Anonymizer {
    MaskingFunction AddRelativeNoiseMF = mfCatalog.getByName("ADD_RELATIVE_NOISE");
    MaskingFunction BlurPhoneMF = mfCatalog.getByName("BLUR_PHONE");
    MaskingFunction BlurZip = mfCatalog.getByName("BLUR_ZIP");
    MaskingFunction BucketizeAgeMF = mfCatalog.getByName("BUCKETIZE_AGE");
    MaskingFunction GeneralizeDiagnosis = mfCatalog.getByName("GENERALIZE_DIAGNOSIS");
    @Override
    public String anonymize(String value) {
        String result = "";
        // add a lot of relative noise
        // blur phone
        // blur zip
        // bucketize age
        // generalize diagnosis
        return result;
    }
}
