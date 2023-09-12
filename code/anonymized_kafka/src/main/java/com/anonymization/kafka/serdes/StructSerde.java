package com.anonymization.kafka.serdes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class StructSerde implements Serializer<Struct>, Deserializer<Struct>, Serde<Struct> {

    private final Schema schema;
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public StructSerde(Schema schema) {
        this.schema = schema;
    }

    @Override
    public Struct deserialize(String s, byte[] bytes) {
        if (bytes == null) return null;

        try {
            String jsonString = new String(bytes, StandardCharsets.UTF_8);
            return toStructFromString(jsonString);
        } catch (Exception e) {
            throw new SerializationException("Error deserializing value", e);
        }
    }

    @Override
    public byte[] serialize(String s, Struct struct) {
        if (struct == null) return null;

        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();

        for (Field field : struct.schema().fields()) {
            String fieldName = field.name();
            Object fieldValue = struct.get(field);

            if (fieldValue != null) {
                switch (field.schema().type()) {
                    case STRING:
                        objectNode.put(fieldName, fieldValue.toString());
                        break;
                    case INT32:
                        objectNode.put(fieldName, (Integer) fieldValue);
                        break;
                    case INT64:
                        objectNode.put(fieldName, (Long) fieldValue);
                        break;
                    case BOOLEAN:
                        objectNode.put(fieldName, (Boolean) fieldValue);
                        break;
                    case FLOAT32:
                        objectNode.put(fieldName, (Float) fieldValue);
                        break;
                    case FLOAT64:
                        objectNode.put(fieldName, (Double) fieldValue);
                        break;
                    default:
                        throw new SerializationException("Schema Type not supported" + field.schema().type());
                }
            }
        }

        try {
            return OBJECT_MAPPER.writeValueAsBytes(objectNode);
        } catch (Exception e) {
            throw new SerializationException("Error serializing value", e);
        }
    }

    private Struct toStructFromString(String line) {
        Struct resultingStruct = new Struct(schema);

        try {
            JsonNode rootNode = OBJECT_MAPPER.readTree(line);
            for (Field field : schema.fields()) {
                String fieldName = field.name();
                JsonNode fieldValue = rootNode.get(fieldName);
                if (fieldValue != null) {
                    switch (field.schema().type()) {
                        case STRING:
                            resultingStruct.put(field.name(), fieldValue.asText());
                            break;
                        case INT32:
                            resultingStruct.put(field.name(), fieldValue.asInt());
                            break;
                        case INT64:
                            resultingStruct.put(field.name(), fieldValue.asLong());
                            break;
                        case BOOLEAN:
                            resultingStruct.put(field.name(), fieldValue.asBoolean());
                            break;
                        case FLOAT32:
                            resultingStruct.put(field.name(), fieldValue.floatValue());
                            break;
                        case FLOAT64:
                            resultingStruct.put(field.name(), fieldValue.asDouble());
                            break;
                        default:
                            throw new SerializationException("Schema Type not supported" + field.schema().type());
                    }
                }
            }
        } catch (Exception e) {
            throw new SerializationException("JSON could not be converted into Struct");
        }
        return resultingStruct;
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
