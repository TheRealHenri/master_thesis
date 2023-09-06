package com.anonymization.kafka.configs.global.schemas;

import com.anonymization.kafka.configs.global.schemas.avro.AvroSchema;
import com.anonymization.kafka.configs.global.schemas.struct.KafkaStructSchema;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.kafka.connect.data.Schema;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "schemaType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AvroSchema.class, name = "AVRO"),
        @JsonSubTypes.Type(value = KafkaStructSchema.class, name = "KAFKA_STRUCT")
})
public interface DataSchema {
    SchemaType getSchemaType();
    SchemaCommon getSchema();
    Schema getKafkaSchema();
}
