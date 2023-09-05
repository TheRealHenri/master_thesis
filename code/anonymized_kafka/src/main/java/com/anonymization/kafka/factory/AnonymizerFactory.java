package com.anonymization.kafka.factory;

import com.anonymization.kafka.anonymizers.Anonymizer;
import com.anonymization.kafka.configs.global.schemas.SchemaCommon;
import com.anonymization.kafka.configs.stream.AnonymizerConfig;
import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.registry.AnonymizerRegistry;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class AnonymizerFactory {
    public static Anonymizer createAnonymizer(AnonymizerConfig anonymizerConfig, SchemaCommon schemaCommon) {
        Class<? extends Anonymizer> anonymizerClass = AnonymizerRegistry.getClassFrom(anonymizerConfig.getAnonymizer());
        Anonymizer anonymizer = null;
        try {
            anonymizer = anonymizerClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Anonymizer " + anonymizerConfig.getAnonymizer() + " could not be instantiated.");
        }
        List<Parameter> parameters = anonymizerConfig.getParameters();
        anonymizer.initialize(parameters);
        return anonymizer;
    }
}
