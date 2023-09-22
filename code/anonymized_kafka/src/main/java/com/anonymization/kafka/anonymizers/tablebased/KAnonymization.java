package com.anonymization.kafka.anonymizers.tablebased;

import com.anonymization.kafka.anonymizers.WindowConfig;
import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.configs.stream.ParameterType;
import com.anonymization.kafka.configs.stream.QuasiIdentifier;
import com.anonymization.kafka.validators.KeyValidator;
import com.anonymization.kafka.validators.ParameterExpectation;
import com.anonymization.kafka.validators.PositiveIntegerValidator;
import org.apache.kafka.connect.data.Struct;

import java.util.Collections;
import java.util.List;

public class KAnonymization implements TableBasedAnonymizer {

    private List<String> keysToSuppress = Collections.emptyList();
    private int k = 0;
    private int delta = 0;
    private int mu = 0;
    private int beta = 0;
    private List<QuasiIdentifier> qis = Collections.emptyList();


    @Override
    public List<Struct> anonymize(List<Struct> lineS) {
        return null;
    }

    @Override
    public List<ParameterExpectation> getParameterExpectations() {
        // TODO handle QIS
        return List.of(
                new ParameterExpectation(
                        ParameterType.KEYS.getName(),
                        List.of(new KeyValidator()),
                        false
                ),
                new ParameterExpectation(
                        ParameterType.K.getName(),
                        List.of(new PositiveIntegerValidator()),
                        true
                ),
                new ParameterExpectation(
                        ParameterType.DELTA.getName(),
                        List.of(new PositiveIntegerValidator()),
                        true
                ),
                new ParameterExpectation(
                        ParameterType.MU.getName(),
                        List.of(new PositiveIntegerValidator()),
                        true
                ),
                new ParameterExpectation(
                        ParameterType.BETA.getName(),
                        List.of(new PositiveIntegerValidator()),
                        true
                ),
                new ParameterExpectation(
                        ParameterType.QUASI_IDENTIFIERS.getName(),
                        Collections.emptyList(),
                        true
                )
        );
    }

    @Override
    public void initialize(List<Parameter> parameters) {
        for (Parameter param : parameters) {
            switch (param.getType()) {
                case KEYS:
                    this.keysToSuppress = param.getKeys();
                    break;
                case K:
                    this.k = param.toInt();
                    break;
                case DELTA:
                    this.delta = param.getDelta();
                    break;
                case MU:
                    this.mu = param.getMu();
                    break;
                case BETA:
                    this.beta = param.getBeta();
                    break;
                case QUASI_IDENTIFIERS:
                    this.qis = param.getQuasiIdentifiers();
                    break;
            }
        }
        // TODO create tree structure out of qis
    }

    public KAnonymization() {}

    @Override
    public WindowConfig getWindowConfig() {
        return null;
    }
}
