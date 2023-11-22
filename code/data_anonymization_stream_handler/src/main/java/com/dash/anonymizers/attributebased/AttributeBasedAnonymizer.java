package com.dash.anonymizers.attributebased;

import com.dash.AnonymizationCategory;
import com.dash.anonymizers.Anonymizer;

public interface AttributeBasedAnonymizer extends Anonymizer {
    @Override
    default AnonymizationCategory getAnonymizationCategory() {
        return AnonymizationCategory.ATTRIBUTE_BASED;
    }
}
