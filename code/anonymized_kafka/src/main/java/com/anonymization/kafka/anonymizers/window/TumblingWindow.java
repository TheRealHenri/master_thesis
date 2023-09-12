package com.anonymization.kafka.anonymizers.window;

import java.time.Duration;
import java.util.Optional;

public class TumblingWindow implements WindowConfig {

    private final Duration windowSize;
    private Optional<Duration> gracePeriod = Optional.empty();

    public TumblingWindow(Duration windowSize) {
        this.windowSize = windowSize;
    }

    public TumblingWindow(Duration windowSize, Duration gracePeriod) {
        this.windowSize = windowSize;
        this.gracePeriod = Optional.of(gracePeriod);
    }

    @Override
    public Duration getWindowSize() {
        return windowSize;
    }

    @Override
    public WindowType getWindowType() {
        return WindowType.TUMBLING;
    }

    @Override
    public Duration getGracePeriod() {
        return gracePeriod.orElseGet(WindowConfig.super::getGracePeriod);
    }
}
