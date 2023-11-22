package com.dash.anonymizers.tablebased;

import com.dash.AnonymizationCategory;
import com.dash.anonymizers.Anonymizer;
import org.apache.kafka.connect.data.Struct;

import java.util.List;

public interface TableBasedAnonymizer extends Anonymizer {

    List<Struct> anonymize(List<Struct> lineS, int position);
    @Override
    default AnonymizationCategory getAnonymizationCategory() {
        return AnonymizationCategory.TABLE_BASED;
    }
}
