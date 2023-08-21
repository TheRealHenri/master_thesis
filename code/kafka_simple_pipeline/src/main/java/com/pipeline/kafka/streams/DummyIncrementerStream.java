package com.pipeline.kafka.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import static com.pipeline.kafka.utils.ConfigConstants.BOOTSTRAP_SERVER;
import static com.pipeline.kafka.utils.ConfigConstants.DEFAULT_TOPIC;

public class DummyIncrementerStream {

    private static final int numberOfTopics = 5;

    public static void main(String[] args) {
        final StreamsBuilder builder = new StreamsBuilder();

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "incrementer-stream");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());


        KStream<String, String> source = builder.stream(DEFAULT_TOPIC);

        for(int i = 1; i < numberOfTopics; i++) {
            int finalI = i;
            source.mapValues(value -> Integer.toString(Integer.parseInt(value) + finalI))
                    .to(DEFAULT_TOPIC + "-" + i);
        }

        final Topology topology = builder.build();

        System.out.println(topology.describe());

        final KafkaStreams streams = new KafkaStreams(topology, props);

        final CountDownLatch latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
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
}


