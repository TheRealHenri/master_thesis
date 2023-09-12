package com.anonymization.kafka.anonymizers.attributebased;

import com.anonymization.kafka.anonymizers.window.SlidingWindow;
import com.anonymization.kafka.anonymizers.window.TumblingWindow;
import com.anonymization.kafka.anonymizers.window.WindowConfig;
import com.anonymization.kafka.anonymizers.window.WindowType;
import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.configs.stream.ParameterType;
import com.anonymization.kafka.validators.*;
import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Aggregation implements AttributeBasedAnonymizer {

    private List<String> keysToAggregate = Collections.emptyList();
    private WindowType windowType;
    private Duration windowSize = Duration.ZERO;
    private Optional<Duration> advanceTime = Optional.empty();
    private Optional<Duration> gracePeriod = Optional.empty();
    private final Logger log = LoggerFactory.getLogger(Aggregation.class);
    private static int windowTracker = 0;

    @Override
    public List<Struct> anonymize(List<Struct> lineS) {
        log.info(windowTracker + lineS.toString());
        windowTracker++;
        return lineS;
    }

    @Override
    public List<ParameterExpectation> getParameterExpectations() {
        return List.of(
                new ParameterExpectation(
                        ParameterType.KEYS.getName(),
                        List.of(new KeyValidator()),
                        true
                ),
                new ParameterExpectation(
                        ParameterType.WINDOW_TYPE.getName(),
                        List.of(new EnumValidator(WindowType.class)),
                        true
                ),
                new ParameterExpectation(
                        ParameterType.WINDOW_SIZE.getName(),
                        List.of(new PositiveIntegerValidator()),
                        true
                ),
                new ParameterExpectation(
                        ParameterType.ADVANCE_TIME.getName(),
                        List.of(new PositiveIntegerValidator()),
                        false
                ),
                new ParameterExpectation(
                        ParameterType.GRACE_PERIOD.getName(),
                        List.of(new PositiveIntegerValidator()),
                        false
                )
        );
    }

    @Override
    public void initialize(List<Parameter> parameters) {
        for (Parameter param : parameters) {
            switch (param.getType()) {
                case KEYS:
                    this.keysToAggregate = param.getKeys();
                    break;
                case WINDOW_TYPE:
                    this.windowType = WindowType.getByName(param.getWindowType());
                    break;
                case WINDOW_SIZE:
                    this.windowSize = Duration.ofMillis(param.getWindowSize());
                    break;
                case ADVANCE_TIME:
                    this.advanceTime = Optional.of(Duration.ofMillis(param.getAdvanceTime()));
                    break;
                case GRACE_PERIOD:
                    this.gracePeriod = Optional.of(Duration.ofMillis(param.getGracePeriod()));
                    break;
            }
        }
    }

    public Aggregation() {
    }

    @Override
    public WindowConfig getWindowConfig() {
        switch (windowType) {
            case SLIDING:
                if (advanceTime.isPresent()) {
                    if (advanceTime.get().compareTo(windowSize) > 0) {
                        throw new IllegalArgumentException("AdvanceTime cannot be greater than window Size!");
                    }
                    return gracePeriod.map(duration -> new SlidingWindow(windowSize, advanceTime.get(), duration)).orElseGet(() -> new SlidingWindow(windowSize, advanceTime.get()));
                } else {
                    throw new IllegalArgumentException("Sliding Window requires the specification of the advanceTime!");
                }
            case TUMBLING:
                return gracePeriod.map(duration -> new TumblingWindow(windowSize, duration)).orElseGet(() -> new TumblingWindow(windowSize));
            default:
                return null;
        }
    }
}
