package com.anonymization.kafka;

import com.anonymization.kafka.streams.AnonymizationStream;
import com.anonymization.kafka.streams.StreamState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class StreamManager {

    private static StreamManager instance;
    private Set<AnonymizationStream> streams;
    private Logger log = LoggerFactory.getLogger(StreamManager.class);

    public static StreamManager getInstance() {
        if (instance == null) {
            synchronized (StreamManager.class) {
                if (instance == null) {
                    instance = new StreamManager();
                }
            }
        }
        return instance;
    }

    public void initializeStreams() {
        // parse config
        // build StreamConfigs
        // initialize streams
        // start all streams
        
    }
    public void startAllStreams() {
        for (AnonymizationStream stream : streams) {
            if (stream.getState() != StreamState.STARTED) {
                stream.start();
            } else {
                log.warn("Stream {} is already started", stream.getConfig().getApplicationId());
            }
        }
    }

    public void stopAllStreams() {
        for (AnonymizationStream stream : streams) {
            if (stream.getState() != StreamState.STOPPED) {
                stream.stop();
            } else {
                log.warn("Stream {} is already stopped", stream.getConfig().getApplicationId());
            }
        }
    }

    public void pauseAllStreams() {
        for (AnonymizationStream stream : streams) {
            if (stream.getState() != StreamState.PAUSED) {
                stream.pause();
            } else {
                log.warn("Stream {} is already paused", stream.getConfig().getApplicationId());
            }
        }
    }

    public void startStream(String applicationId) {
        for (AnonymizationStream stream : streams) {
            if (stream.getConfig().getApplicationId().equals(applicationId)) {
                if (stream.getState() != StreamState.STARTED) {
                    stream.start();
                } else {
                    log.warn("Stream {} is already started", stream.getConfig().getApplicationId());
                }
                return;
            }
        }
    }

    public void stopStream(String applicationId) {
        for (AnonymizationStream stream : streams) {
            if (stream.getConfig().getApplicationId().equals(applicationId)) {
                if (stream.getState() != StreamState.STOPPED) {
                    stream.stop();
                } else {
                    log.warn("Stream {} is already stopped", stream.getConfig().getApplicationId());
                }
                return;
            }
        }
    }

    public void pauseStream(String applicationId) {
        for (AnonymizationStream stream : streams) {
            if (stream.getConfig().getApplicationId().equals(applicationId)) {
                if (stream.getState() != StreamState.PAUSED) {
                    stream.pause();
                } else {
                    log.warn("Stream {} is already paused", stream.getConfig().getApplicationId());
                }
                return;
            }
        }
    }

    public void listStreams() {
        log.info("Listing all streams:");
        for (AnonymizationStream stream : streams) {
            log.info("Stream {} is in state {}", stream.getConfig().getApplicationId(), stream.getState());
        }
    }
}
