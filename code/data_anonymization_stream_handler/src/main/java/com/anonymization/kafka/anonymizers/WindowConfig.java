package com.anonymization.kafka.anonymizers;

import java.time.Duration;
import java.util.Optional;

public class WindowConfig {
    private final Duration windowSize;
    private final Optional<Duration> advanceTime;
    private final Optional<Duration> gracePeriod;

    public WindowConfig(Duration windowSize, Optional<Duration> advanceTime, Optional<Duration> gracePeriod) {
        this.windowSize = windowSize;
        this.advanceTime = advanceTime;
        this.gracePeriod = gracePeriod;
    }

    public Duration getWindowSize() {
        return windowSize;
    }

    public Duration getAdvanceTime() {
        return advanceTime.orElse(Duration.ZERO);
    }

    public Duration getGracePeriod() {
        return gracePeriod.orElse(Duration.ZERO);
    }
}

