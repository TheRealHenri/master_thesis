package com.anonymization.kafka.builders;

import com.anonymization.kafka.AnonymizationCategory;
import com.anonymization.kafka.anonymizers.Anonymizer;
import com.anonymization.kafka.anonymizers.WindowConfig;
import com.anonymization.kafka.configs.AnonymizationStreamConfig;
import com.anonymization.kafka.configs.global.schemas.SchemaCommon;
import com.anonymization.kafka.configs.stream.AnonymizerConfig;
import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.configs.stream.StreamProperties;
import com.anonymization.kafka.factory.AnonymizerFactory;
import com.anonymization.kafka.registry.AnonymizerRegistry;
import com.anonymization.kafka.validators.ParameterExpectation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.ConfigurationException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AnonymizationStreamConfigBuilder {

    private final SchemaCommon schema;
    private AnonymizationCategory anonymizationCategory = null;
    private static final Logger log = LoggerFactory.getLogger(AnonymizationStreamConfigBuilder.class);

    public AnonymizationStreamConfigBuilder(SchemaCommon schema) {
        this.schema = schema;
    }

    public AnonymizationStreamConfig build(StreamProperties streamProperties) throws ConfigurationException {

        anonymizationCategory = null;

        log.info("Building Config for Stream {}", streamProperties.getApplicationId());

        validateStreamProperties(streamProperties);

        List<Anonymizer> anonymizers = getValidatedInstantiatedAnonymizers(streamProperties);

        if (anonymizationCategory.equals(AnonymizationCategory.ATTRIBUTE_BASED)) {
            ensureOnlyOneWindowConfig(anonymizers);
        }

        log.info("Config for Stream {} built successfully.", streamProperties.getApplicationId());

        return new AnonymizationStreamConfig(streamProperties.getApplicationId(), anonymizers, anonymizationCategory);
    }

    private void validateStreamProperties(StreamProperties streamProperties) throws ConfigurationException {
        if (streamProperties.getApplicationId().isEmpty()) {
            throw new ConfigurationException("Application id must be defined. Stream must have name.");
        }
        if (streamProperties.getApplicationId().contains(" ")) {
            throw new ConfigurationException("Application id must not contain spaces.");
        }
        if (streamProperties.getAnonymizers() == null || streamProperties.getAnonymizers().isEmpty()) {
            throw new ConfigurationException("Define at least one Anonymizer per Stream.");
        }
        log.info("Stream properties validated.");
    }

    private List<Anonymizer> getValidatedInstantiatedAnonymizers(StreamProperties streamProperties) throws ConfigurationException {
        log.info("Validating and instantiating Anonymizers.");
        List<AnonymizerConfig> anonymizerConfigs = streamProperties.getAnonymizers();
        List<Anonymizer> resultingAnonymizers = new ArrayList<>();
        for (AnonymizerConfig anonymizerConfig : anonymizerConfigs) {
            try {
                Anonymizer currentEmptyAnonymizer = tryToGetAnonymizerInstance(anonymizerConfig);
                validateAnonymizer(currentEmptyAnonymizer, anonymizerConfig);
                Anonymizer initializedAnonymizer = AnonymizerFactory.createAnonymizer(anonymizerConfig);
                resultingAnonymizers.add(initializedAnonymizer);
            } catch (IllegalArgumentException e) {
                throw new ConfigurationException("Anonymizer " + anonymizerConfig.getAnonymizer() + " is not valid: " + e.getMessage());
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Anonymizer " + anonymizerConfig.getAnonymizer() + " could not be instantiated.");
            }
        }
        assert resultingAnonymizers.size() == anonymizerConfigs.size();
        return resultingAnonymizers;
    }

    private Anonymizer tryToGetAnonymizerInstance(AnonymizerConfig anonymizerConfig) throws ConfigurationException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<? extends Anonymizer> anonymizerClass = AnonymizerRegistry.getClassFrom(anonymizerConfig.getAnonymizer());
        Anonymizer currentEmptyAnonymizer = anonymizerClass.getDeclaredConstructor().newInstance();
        if (anonymizationCategory == null) {
            anonymizationCategory = currentEmptyAnonymizer.getAnonymizationCategory();
        } else if (anonymizationCategory != currentEmptyAnonymizer.getAnonymizationCategory()) {
            throw new ConfigurationException("Anonymizers in one Stream must be of the same category.");
        }
        return currentEmptyAnonymizer;
    }
    private void validateAnonymizer(Anonymizer specifiedAnonymizers, AnonymizerConfig anonConfig) {
        List<String> providedParamNames = anonConfig
                .getParameters()
                .stream()
                .map(parameter -> parameter.getType().getName()
                )
                .collect(Collectors.toList());
        List<String> expectedParameters = new ArrayList<>();
        for (ParameterExpectation parameterExpectation : specifiedAnonymizers.getParameterExpectations()) {
            expectedParameters.add(parameterExpectation.getParamName());

            if (parameterExpectation.isRequired() && !providedParamNames.contains(parameterExpectation.getParamName())) {
                throw new IllegalArgumentException("Parameter " + parameterExpectation.getParamName() + " is required for Anonymizer " + anonConfig.getAnonymizer() + ".");
            }

            if (providedParamNames.contains(parameterExpectation.getParamName())) {
                Parameter providedParameter = anonConfig.getParameters().stream().filter(p -> p.getType().getName().equals(parameterExpectation.getParamName())).findFirst().orElse(null);

                parameterExpectation.validate(providedParameter, schema);
            }
        }
        for (String paramName : providedParamNames) {
            if (!expectedParameters.contains(paramName)) {
                throw new IllegalArgumentException("Unexpected parameter: " + paramName + " for Anonymizer " + anonConfig.getAnonymizer() + ".");
            }
        }
    }
    private void ensureOnlyOneWindowConfig(List<Anonymizer> anonymizers) throws ConfigurationException {
        WindowConfig expectedWindowConfig = null;
        for (Anonymizer anonymizer : anonymizers) {
            WindowConfig currentWindowConfig = anonymizer.getWindowConfig();
            if (currentWindowConfig == null) {
                throw new ConfigurationException("No Window Config found for anonymizer " + anonymizer);
            }
            if (expectedWindowConfig == null) {
                expectedWindowConfig = currentWindowConfig;
            } else {
                if (!expectedWindowConfig.getWindowSize().equals(currentWindowConfig.getWindowSize())) {
                    throw new ConfigurationException("Window config must be the same for all anonymizers of a single stream. Expected window size" + expectedWindowConfig.getWindowSize() + " but found " + currentWindowConfig.getWindowSize());
                } else if (!expectedWindowConfig.getGracePeriod().equals(currentWindowConfig.getGracePeriod())) {
                    throw new ConfigurationException("Window config must be the same for all anonymizers of a single stream. Expected grace period " + expectedWindowConfig.getGracePeriod() + " but found " + currentWindowConfig.getGracePeriod());
                } else if (!expectedWindowConfig.getAdvanceTime().equals(currentWindowConfig.getAdvanceTime())) {
                    throw new ConfigurationException("Window config must be the same for all anonymizers of a single stream. Expected advance time " + expectedWindowConfig.getAdvanceTime() + " but found " + currentWindowConfig.getAdvanceTime());
                }
            }
        }
    }
}
