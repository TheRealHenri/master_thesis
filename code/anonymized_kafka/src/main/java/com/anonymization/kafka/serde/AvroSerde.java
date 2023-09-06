package com.anonymization.kafka.serde;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.data.Struct;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class AvroSerde implements Serde<Struct>, Deserializer<Struct>, Serializer<Struct> {

    private final Schema schema;

    public AvroSerde(Schema schema) {
        this.schema = schema;
    }

    @Override
    public byte[] serialize(String topic, Struct struct) {
        GenericRecord avroRecord = structToGenericRecord(struct);
        return genericRecordToByteArray(avroRecord);
    }

    @Override
    public Struct deserialize(String topic, byte[] bytes) {
        GenericRecord avroRecord = byteArrayToGenericRecord(bytes);
        return genericRecordToStruct(avroRecord);
    }

    private byte[] genericRecordToByteArray(GenericRecord record) {
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Encoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
            datumWriter.write(record, encoder);
            encoder.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new SerializationException("Error serializing Avro message", e);
        }
    }

    private GenericRecord byteArrayToGenericRecord(byte[] bytes) {
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            Decoder decoder = DecoderFactory.get().binaryDecoder(inputStream, null);
            return datumReader.read(null, decoder);
        } catch (IOException e) {
            throw new SerializationException("Error deserializing Avro message", e);
        }
    }

    private GenericRecord structToGenericRecord(Struct struct) {
        return null;
    }

    private Struct genericRecordToStruct(GenericRecord avroRecord) {
        return null;
    }

    @Override
    public Serializer<Struct> serializer() {
        return this;
    }

    @Override
    public Deserializer<Struct> deserializer() {
        return this;
    }
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public void close() {}
}
