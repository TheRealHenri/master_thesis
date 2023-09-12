package com.anonymization.kafka.anonymizers.window;

import java.time.Duration;

public interface WindowConfig {
    Duration getWindowSize();
    WindowType getWindowType();
    default Duration getGracePeriod() {
        return Duration.ofSeconds(1);
    };
}

