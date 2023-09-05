package com.anonymization.kafka.factory;

import com.anonymization.kafka.configs.AnonymizationStreamConfig;
import com.anonymization.kafka.configs.global.GlobalConfig;
import com.anonymization.kafka.serde.StructSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

import java.util.List;
import java.util.Properties;

public class AnonymizationStreamFactory {

    public static KafkaStreams buildAnonymizationStream(GlobalConfig globalConfig, AnonymizationStreamConfig streamConfig) {
        Properties props = createPropertiesFrom(globalConfig, streamConfig);

        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Struct> source = builder.stream(globalConfig.getTopic());

        switch (streamConfig.getCategory()) {
            case VALUE_BASED:
            case TUPLE_BASED:
                source.mapValues(value -> streamConfig.getAnonymizers().stream().map(anonymizer -> anonymizer.anonymize(List.of(value))))
                    .to(globalConfig.getTopic() + "-" + streamConfig.getApplicationId());
                break;
            case ATTRIBUTE_BASED:
            case TABLE_BASED:
            default:
                source.mapValues(value -> value).to(globalConfig.getTopic() + "-" + streamConfig.getApplicationId());
        }

        final Topology topology = builder.build();
        return new KafkaStreams(topology, props);
    }

    private static Properties createPropertiesFrom(GlobalConfig globalConfig, AnonymizationStreamConfig streamConfig) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, streamConfig.getApplicationId());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, globalConfig.getBootstrapServer());
        // adapt these probably
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, StructSerde.class);
        return props;
    }

}
