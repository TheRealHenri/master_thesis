package com.dash.anonymizers.valuebased;

import com.dash.AnonymizationCategory;
import com.dash.anonymizers.Anonymizer;
import com.dash.anonymizers.WindowConfig;

public interface ValueBasedAnonymizer extends Anonymizer {
    @Override
    default AnonymizationCategory getAnonymizationCategory() {
        return AnonymizationCategory.VALUE_BASED;
    }
    @Override
    default WindowConfig getWindowConfig() { return null; }
}
