package com.dash.configs.global.schemas;

import com.dash.configs.global.schemas.avro.AvroSchema;
import com.dash.configs.global.schemas.struct.KafkaStructSchema;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.connect.data.Struct;

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
    Serde<Struct> getSerde();
}
