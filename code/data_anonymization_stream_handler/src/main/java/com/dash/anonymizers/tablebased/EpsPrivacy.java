package com.dash.anonymizers.tablebased;

import com.dash.anonymizers.WindowConfig;
import com.dash.configs.stream.Parameter;
import com.dash.configs.stream.ParameterType;
import com.dash.validators.ParameterExpectation;
import com.dash.validators.PositiveIntegerValidator;
import org.apache.kafka.connect.data.Struct;

import java.util.List;
import java.util.Objects;

public class EpsPrivacy implements TableBasedAnonymizer {

    private int windowSize = 0;

    @Override
    public List<Struct> anonymize(List<Struct> lineS) {
        return null;
    }
    @Override
    public List<Struct> anonymize(List<Struct> lineS, int position) {
        return null;
    }

    @Override
    public List<ParameterExpectation> getParameterExpectations() {
        return List.of(
                new ParameterExpectation(
                        ParameterType.WINDOW_SIZE.getName(),
                        List.of(new PositiveIntegerValidator()),
                        true
                )
        );
    }

    @Override
    public void initialize(List<Parameter> parameters) {
        for (Parameter param : parameters) {
            if (Objects.requireNonNull(param.getType()) == ParameterType.WINDOW_SIZE) {
                this.windowSize = param.toInt();
            }
        }
    }

    public EpsPrivacy() {}

    @Override
    public WindowConfig getWindowConfig() {
        return null;
    }

}
