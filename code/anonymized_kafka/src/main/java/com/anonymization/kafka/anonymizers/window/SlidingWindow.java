package com.anonymization.kafka.anonymizers.window;

import java.time.Duration;
import java.util.Optional;

public class SlidingWindow implements WindowConfig {
    private final Duration windowSize;
    private final Duration advanceTime;
    private Optional<Duration> gracePeriod = Optional.empty();

    public SlidingWindow(Duration windowSize, Duration advanceTime) {
        this.windowSize = windowSize;
        this.advanceTime = advanceTime;
    }

    public SlidingWindow(Duration windowSize, Duration advanceTime, Duration gracePeriod) {
        this.windowSize = windowSize;
        this.advanceTime = advanceTime;
        this.gracePeriod = Optional.of(gracePeriod);
    }

    @Override
    public Duration getWindowSize() {
        return windowSize;
    }

    public Duration getAdvanceTime() { return advanceTime; }

    @Override
    public WindowType getWindowType() {
        return WindowType.SLIDING;
    }

    @Override
    public Duration getGracePeriod() {
        return gracePeriod.orElseGet(WindowConfig.super::getGracePeriod);
    }
}
