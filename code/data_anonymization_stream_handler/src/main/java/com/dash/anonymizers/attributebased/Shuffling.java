package com.dash.anonymizers.attributebased;

import com.dash.anonymizers.WindowConfig;
import com.dash.configs.stream.Parameter;
import com.dash.configs.stream.ParameterType;
import com.dash.validators.KeyValidator;
import com.dash.validators.ParameterExpectation;
import com.dash.validators.PositiveIntegerValidator;
import com.dash.validators.PositiveLongValidator;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class Shuffling implements AttributeBasedAnonymizer {

    private List<String> keysToShuffle = Collections.emptyList();
    private Optional<Long> seed = Optional.empty();
    private boolean shuffleIndividually = false;
    private Duration windowSize = Duration.ZERO;
    private Optional<Duration> advanceTime = Optional.empty();
    private Optional<Duration> gracePeriod = Optional.empty();
    private final Logger log = LoggerFactory.getLogger(Shuffling.class);

    @Override
    public List<Struct> anonymize(List<Struct> lineS) {
        if (lineS.isEmpty()) {
            log.info("Empty window");
        }

        Struct sampleStruct = lineS.get(0);

        for (Field field : sampleStruct.schema().fields()) {
            Random random = seed.map(Random::new).orElseGet(Random::new);
            for (String key : keysToShuffle) {
                if (field.name().equals(key)) {
                    if (shuffleIndividually) {
                        random = seed.map(Random::new).orElseGet(Random::new);
                        if (keysToShuffle.size() < 2) {
                            log.error("Unnecessary Parameter shuffleIndividually for only one key");
                        }
                        if (seed.isPresent()) {
                            log.error("Invalid Configuration! Seed is given, while shuffling individually is enabled. Using Seed.");
                        }
                    }
                    List<Object> attributeList = lineS.stream().map(struct -> struct.get(key)).collect(Collectors.toList());
                    Collections.shuffle(attributeList, random);
                    for (int i = 0; i < lineS.size(); i++) {
                        lineS.get(i).put(key, attributeList.get(i));
                    }
                }
            }
        }

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
                        ParameterType.SEED.getName(),
                        List.of(new PositiveLongValidator()),
                        false
                ),
                new ParameterExpectation(
                        ParameterType.SHUFFLE_INDIVIDUALLY.getName(),
                        Collections.emptyList(),
                        false
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
                    this.keysToShuffle = param.getKeys();
                    break;
                case SEED:
                    this.seed = Optional.of(param.getSeed());
                    break;
                case SHUFFLE_INDIVIDUALLY:
                    this.shuffleIndividually = param.getShuffleIndividually();
                    break;
                case WINDOW_SIZE:
                    this.windowSize = Duration.ofNanos(param.getWindowSize());
                    break;
                case ADVANCE_TIME:
                    this.advanceTime = Optional.of(Duration.ofNanos(param.getAdvanceTime()));
                    break;
                case GRACE_PERIOD:
                    this.gracePeriod = Optional.of(Duration.ofNanos(param.getGracePeriod()));
                    break;
            }
        }
    }

    public Shuffling() {
    }

    @Override
    public WindowConfig getWindowConfig() {
        return new WindowConfig(windowSize, advanceTime, gracePeriod);
    }
}
