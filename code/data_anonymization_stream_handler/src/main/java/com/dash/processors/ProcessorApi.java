package com.dash.processors;

import com.dash.anonymizers.Anonymizer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ProcessorApi {
    public static class CountBasedWindowingProcessorSupplier implements ProcessorSupplier<String, Struct, String, Struct> {
        private final String storeName;
        private final List<Anonymizer> anonymizers;
        private final Serde<Struct> structSerde;

        public CountBasedWindowingProcessorSupplier(List<Anonymizer> anonymizers, String storeName, Serde<Struct> structSerde) {
            this.anonymizers = anonymizers;
            this.storeName = storeName;
            this.structSerde = structSerde;
        }

        @Override
        public Processor<String, Struct, String, Struct> get() {
            return new Processor<>() {
                private ProcessorContext<String, Struct> context;
                private KeyValueStore<String, byte[]> kvStore;

                @Override
                public void init(ProcessorContext<String, Struct> context) {
                    this.context = context;
                    this.kvStore = context.getStateStore(storeName);
                    this.context.schedule(Duration.ofSeconds(15), PunctuationType.STREAM_TIME, this::forwardAll);
                }

                private void forwardAll(final long timestamp) {
                    try (KeyValueIterator<String, byte[]> iterator = kvStore.all()) {
                        while (iterator.hasNext()) {
                            final KeyValue<String,  byte[]> nextKV = iterator.next();
                            List<Struct> deserializedList = deserializeList(nextKV.value);
                            List<Struct> anonymizedValues = anonymizeValues(deserializedList);
                            anonymizedValues.forEach(anonymizedValue ->
                                    context.forward(new Record<>(nextKV.key, anonymizedValue, timestamp))
                            );
                            kvStore.delete(nextKV.key);
                        }
                    }
                }

                @Override
                public void process(Record<String, Struct> record) {
                    final String key = record.key();
                    byte[] existingValue = kvStore.get(key);
                    List<Struct> values = existingValue == null ? new ArrayList<>() : deserializeList(existingValue);
                    values.add(record.value());
                    kvStore.put(key, serializeList(values));
                }


                private List<Struct> anonymizeValues(List<Struct> values) {
                    for (Anonymizer anonymizer : anonymizers) {
                        values = anonymizer.anonymize(values);
                    }
                    return values;
                }

                private byte[] serializeList(List<Struct> structs) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(baos);

                    for (Struct struct : structs) {
                        try {
                            byte[] structBytes = structSerde.serializer().serialize("", struct);
                            out.writeInt(structBytes.length);
                            out.write(structBytes);
                        } catch (IOException e) {
                            System.out.println("Error serializing struct: " + e.getMessage());
                        }
                    }

                    return baos.toByteArray();
                }

                private List<Struct> deserializeList(byte[] bytes) {
                    List<Struct> structs = new ArrayList<>();
                    ByteBuffer buffer = ByteBuffer.wrap(bytes);

                    while (buffer.hasRemaining()) {
                        int structSize = buffer.getInt();
                        byte[] structBytes = new byte[structSize];
                        buffer.get(structBytes);
                        Struct struct = structSerde.deserializer().deserialize("", structBytes);
                        structs.add(struct);
                    }

                    return structs;
                }

            };
        }
        @Override
        public Set<StoreBuilder<?>> stores() {
            return Collections.singleton(anonymizerStoreBuilder);
        }
    }
    final static String storeName = "anonymizer-store";
    static StoreBuilder<KeyValueStore<String, byte[]>> anonymizerStoreBuilder = Stores.keyValueStoreBuilder(
            Stores.persistentKeyValueStore(storeName),
            Serdes.String(),
            Serdes.ByteArray()
    );
}
