package com.anonymization.kafka.registry;

import com.anonymization.kafka.anonymizers.Anonymizer;
import com.anonymization.kafka.anonymizers.attributebased.Aggregation;
import com.anonymization.kafka.anonymizers.attributebased.Averaging;
import com.anonymization.kafka.anonymizers.attributebased.Shuffling;
import com.anonymization.kafka.anonymizers.attributebased.UnivariantMicroAggregation;
import com.anonymization.kafka.anonymizers.tablebased.*;
import com.anonymization.kafka.anonymizers.tuplebased.ConditionalSubstitution;
import com.anonymization.kafka.anonymizers.valuebased.*;

import java.util.HashMap;

public final class AnonymizerRegistry {

    private static final HashMap<String, Class<? extends Anonymizer>> REGISTRY = new HashMap<>();

    static {
        // attribute based
        REGISTRY.put("aggregation", Aggregation.class);
        REGISTRY.put("averaging", Averaging.class);
        REGISTRY.put("shuffling", Shuffling.class);
        REGISTRY.put("univariant_micro_aggregation", UnivariantMicroAggregation.class);
        // table based
        REGISTRY.put("eps_privacy", EpsPrivacy.class);
        REGISTRY.put("k_anonymization", KAnonymization.class);
        REGISTRY.put("l_diversity", LDiversity.class);
        REGISTRY.put("multivariant_micro_aggregation", MultivariantMicroAggregation.class);
        REGISTRY.put("t_closeness", TCloseness.class);
        // tuple based
        REGISTRY.put("conditional_substitution", ConditionalSubstitution.class);
        // value based
        REGISTRY.put("blurring", Blurring.class);
        REGISTRY.put("bucketizing", Bucketizing.class);
        REGISTRY.put("generalization", Generalization.class);
        REGISTRY.put("noise_methods", NoiseMethods.class);
        REGISTRY.put("substitution", Substitution.class);
        REGISTRY.put("suppression", Suppression.class);
        REGISTRY.put("tokenization", Tokenization.class);
    }

    public static Class<? extends Anonymizer> getClassFrom(String name) {
        return REGISTRY.get(name);
    }

    // not to be initialized
    private AnonymizerRegistry() {}

}
