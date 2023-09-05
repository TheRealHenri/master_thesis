package com.anonymization.kafka.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.data.Struct;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class StructSerde implements Serializer<Struct>, Deserializer<Struct>, Serde<Struct> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public byte[] serialize(String topic, Struct struct) {
        if (struct == null) {
            return null;
        }
        try {
            return struct.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SerializationException("Error serializing struct", e);
        }
    }

    @Override
    public Struct deserialize(String topic, byte[] data) {
        if (data == null) return null;

        try {
            return OBJECT_MAPPER.readValue(data, Struct.class);
        } catch (Exception e) {
            throw new SerializationException("Error deserializing data", e);
        }
    }

    @Override
    public void close() {}



    @Override
    public Serializer<Struct> serializer() {
        return this;
    }

    @Override
    public Deserializer<Struct> deserializer() {
        return this;
    }
}
