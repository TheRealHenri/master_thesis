package com.dash.factory;

import com.dash.anonymizers.Anonymizer;
import com.dash.configs.stream.AnonymizerConfig;
import com.dash.configs.stream.Parameter;
import com.dash.registry.AnonymizerRegistry;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class AnonymizerFactory {
    public static Anonymizer createAnonymizer(AnonymizerConfig anonymizerConfig) {
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
