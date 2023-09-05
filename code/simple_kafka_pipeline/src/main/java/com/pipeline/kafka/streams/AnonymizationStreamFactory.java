package com.pipeline.kafka.streams;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import static com.pipeline.kafka.utils.ConfigConstants.BOOTSTRAP_SERVER;
import static com.pipeline.kafka.utils.ConfigConstants.DEFAULT_TOPIC;

public class AnonymizationStreamFactory {

    public static void createAnonymizationStream(AnonymizationStreamConfig config) {
        final StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> source = builder.stream(DEFAULT_TOPIC);

        source.mapValues(config.anonymizer::anonymize)
                .to(DEFAULT_TOPIC + "-" + config.applicationId);

        final Topology topology = builder.build();
        Properties streamProps = config.getStreamProperties();
        streamProps.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        final KafkaStreams streams = new KafkaStreams(topology, config.getStreamProperties());
        final CountDownLatch latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook-" + config.applicationId) {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);

    }

    public static void main(String[] args) {
        AnonymizationStreamConfig lvl1Config = new AnonymizationStreamConfig("lvl1-anon-stream", new Lvl1Anonymizer());
        AnonymizationStreamConfig lvl2Config = new AnonymizationStreamConfig("lvl2-anon-stream", new Lvl2Anonymizer());
        AnonymizationStreamConfig lvl3Config = new AnonymizationStreamConfig("lvl3-anon-stream", new Lvl3Anonymizer());
        AnonymizationStreamConfig lvl4Config = new AnonymizationStreamConfig("lvl4-anon-stream", new Lvl4Anonymizer());
        createAnonymizationStream(lvl1Config);
        createAnonymizationStream(lvl2Config);
        createAnonymizationStream(lvl3Config);
        createAnonymizationStream(lvl4Config);
    }
}
