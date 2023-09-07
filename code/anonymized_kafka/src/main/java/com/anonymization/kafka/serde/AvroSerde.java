package com.anonymization.kafka.serde;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
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
        GenericRecord resultRecord = new GenericData.Record(schema);

        for (Field field : struct.schema().fields()) {
            resultRecord.put(field.name(), struct.get(field));
        }

        return resultRecord;
    }

    private Struct genericRecordToStruct(GenericRecord avroRecord) {
        org.apache.kafka.connect.data.Schema kafkaSchema = convertAvroToKafkaSchema(schema);
        Struct resultStruct = new Struct(kafkaSchema);

        for (Field field : kafkaSchema.fields()) {
            Object value = avroRecord.get(field.name());
            resultStruct.put(field, value);
        }

        return resultStruct;
    }

    public static org.apache.kafka.connect.data.Schema convertAvroToKafkaSchema(Schema avroSchema) {
        SchemaBuilder kafkaSchemaBuilder = SchemaBuilder.struct().name(avroSchema.getName());
        List<Schema.Field> avroFields = avroSchema.getFields();

        for (Schema.Field avroField : avroFields) {
            Schema.Type avroType = avroField.schema().getType();
            String fieldName = avroField.name();
            org.apache.kafka.connect.data.Schema typeSchema;
            switch (avroType) {
                case BOOLEAN:
                    typeSchema = avroField.schema().isNullable() ?
                            org.apache.kafka.connect.data.Schema.OPTIONAL_BOOLEAN_SCHEMA : org.apache.kafka.connect.data.Schema.BOOLEAN_SCHEMA;
                    kafkaSchemaBuilder.field(fieldName, typeSchema);
                    break;
                case INT:
                    typeSchema = avroField.schema().isNullable() ?
                            org.apache.kafka.connect.data.Schema.OPTIONAL_INT32_SCHEMA : org.apache.kafka.connect.data.Schema.INT32_SCHEMA;
                    kafkaSchemaBuilder.field(fieldName, typeSchema);
                    break;
                case LONG:
                    typeSchema = avroField.schema().isNullable() ?
                            org.apache.kafka.connect.data.Schema.OPTIONAL_INT64_SCHEMA : org.apache.kafka.connect.data.Schema.INT64_SCHEMA;
                    kafkaSchemaBuilder.field(fieldName, typeSchema);
                    break;
                case FLOAT:
                    typeSchema = avroField.schema().isNullable() ?
                            org.apache.kafka.connect.data.Schema.OPTIONAL_FLOAT32_SCHEMA : org.apache.kafka.connect.data.Schema.FLOAT32_SCHEMA;
                    kafkaSchemaBuilder.field(fieldName, typeSchema);
                    break;
                case DOUBLE:
                    typeSchema = avroField.schema().isNullable() ?
                            org.apache.kafka.connect.data.Schema.OPTIONAL_FLOAT64_SCHEMA : org.apache.kafka.connect.data.Schema.FLOAT64_SCHEMA;
                    kafkaSchemaBuilder.field(fieldName, typeSchema);
                    break;
                case STRING:
                    typeSchema = avroField.schema().isNullable() ?
                            org.apache.kafka.connect.data.Schema.OPTIONAL_STRING_SCHEMA : org.apache.kafka.connect.data.Schema.STRING_SCHEMA;
                    kafkaSchemaBuilder.field(fieldName, typeSchema);
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported Avro type: " + avroType);
            }
        }
        return kafkaSchemaBuilder.build();
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
