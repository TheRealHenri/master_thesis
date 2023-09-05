package com.pipeline.kafka.streams;

import com.pipeline.kafka.dataMasking.MaskingFunctionsCatalog;

public interface Anonymizer {
    MaskingFunctionsCatalog mfCatalog = new MaskingFunctionsCatalog();
    String anonymize(String value);
}
