package com.dash.anonymizers.tuplebased;

import com.dash.AnonymizationCategory;
import com.dash.anonymizers.Anonymizer;
import com.dash.anonymizers.WindowConfig;

public interface TupleBasedAnonymizer extends Anonymizer {
    @Override
    default AnonymizationCategory getAnonymizationCategory() {
        return AnonymizationCategory.TUPLE_BASED;
    }
    @Override
    default WindowConfig getWindowConfig() { return null; }
}
