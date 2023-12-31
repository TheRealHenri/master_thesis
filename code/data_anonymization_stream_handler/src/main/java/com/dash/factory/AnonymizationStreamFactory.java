package com.dash.factory;

import com.dash.anonymizers.Anonymizer;
import com.dash.anonymizers.WindowConfig;
import com.dash.anonymizers.tablebased.TableBasedAnonymizer;
import com.dash.configs.AnonymizationStreamConfig;
import com.dash.configs.global.GlobalConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AnonymizationStreamFactory {

    private static int eventCounter = 0;

    public static KafkaStreams buildAnonymizationStream(GlobalConfig globalConfig, AnonymizationStreamConfig streamConfig) {
        Properties props = createPropertiesFrom(globalConfig, streamConfig);

        final StreamsBuilder builder = new StreamsBuilder();

        Serde<Struct> structSerde = globalConfig.getDataSchema().getSerde();

        KStream<String, Struct> source = builder.stream(globalConfig.getTopic(), Consumed.with(Serdes.String(), structSerde));

        List<Anonymizer> anonymizers = streamConfig.getAnonymizers();
        if (!anonymizers.isEmpty()) {
            switch (streamConfig.getCategory()) {
                case VALUE_BASED:
                case TUPLE_BASED:
                    source.flatMapValues(value -> {
                        List<Struct> tmpStruct = List.of(value);
                        for (Anonymizer anonymizer : anonymizers) {
                            tmpStruct = anonymizer.anonymize(tmpStruct);
                        }
                        return tmpStruct;
                    }).to(globalConfig.getTopic() + "-" + streamConfig.getApplicationId(), Produced.with(Serdes.String(), structSerde));
                    break;
                case ATTRIBUTE_BASED:
                    TimeWindows timeWindow = extractWindow(anonymizers.get(0).getWindowConfig());
                    source.groupByKey()
                            .windowedBy(timeWindow)
                            .aggregate(
                                    ArrayList::new,
                                    (key, value, aggregate) -> {
                                        aggregate.add(value);
                                        return aggregate;
                                    },
                                    Materialized.with(Serdes.String(), Serdes.ListSerde(ArrayList.class, structSerde))
                            )
                            .toStream((KeyValueMapper<Windowed<String>, List<Struct>, String>) (key, value) -> key.key())
                            .flatMapValues((ValueMapper<List<Struct>, Iterable<Struct>>) values -> {
                                for (Anonymizer anonymizer : anonymizers) {
                                    values = anonymizer.anonymize(values);
                                }
                                return values;
                            }).to(globalConfig.getTopic() + "-" + streamConfig.getApplicationId(), Produced.with(Serdes.String(), structSerde));
                    break;

                case TABLE_BASED:
                    source.flatMapValues(value -> {
                        eventCounter++;
                        List<Struct> tmpStruct = List.of(value);
                        for (Anonymizer anonymizer : anonymizers) {
                            TableBasedAnonymizer tableBasedAnonymizer = (TableBasedAnonymizer) anonymizer;
                            tmpStruct = tableBasedAnonymizer.anonymize(tmpStruct, eventCounter);
                        }
                        return tmpStruct;
                    }).to(globalConfig.getTopic() + "-" + streamConfig.getApplicationId(), Produced.with(Serdes.String(), structSerde));
            }
        } else {
            source.to(globalConfig.getTopic() + "-" + streamConfig.getApplicationId(), Produced.with(Serdes.String(), structSerde));
        }


        final Topology topology = builder.build();
        return new KafkaStreams(topology, props);
    }

    private static Properties createPropertiesFrom(GlobalConfig globalConfig, AnonymizationStreamConfig streamConfig) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, streamConfig.getApplicationId());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, globalConfig.getBootstrapServer());
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return props;
    }

    private static TimeWindows extractWindow(WindowConfig windowConfig) {
        assert windowConfig != null;
        Duration windowSize = windowConfig.getWindowSize();
        Duration gracePeriod = windowConfig.getGracePeriod();
        TimeWindows resultTimeWindows = TimeWindows.ofSizeAndGrace(windowSize, gracePeriod);
        Duration advanceTime = windowConfig.getAdvanceTime();
        if (!advanceTime.isZero()) {
            resultTimeWindows.advanceBy(advanceTime);
        }
        return resultTimeWindows;
    }
}
