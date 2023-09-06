package com.anonymization.kafka.configs.global.schemas.avro;

import com.anonymization.kafka.configs.global.schemas.DataSchema;
import com.anonymization.kafka.configs.global.schemas.FieldType;
import com.anonymization.kafka.configs.global.schemas.SchemaCommon;
import com.anonymization.kafka.configs.global.schemas.SchemaType;
import com.anonymization.kafka.serde.AvroSerde;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.connect.data.Struct;

import java.util.HashMap;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AvroSchema implements DataSchema {
    private String namespace;
    private String type;
    private String name;
    private List<AvroDataSchemaField> fields;

    public AvroSchema() {}

    public AvroSchema(String namespace, String type, String name, List<AvroDataSchemaField> fields) {
        this.namespace = namespace;
        this.type = type;
        this.name = name;
        this.fields = fields;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AvroDataSchemaField> getFields() {
        return fields;
    }

    public void setFields(List<AvroDataSchemaField> fields) {
        this.fields = fields;
    }

    @Override
    public SchemaType getSchemaType() {
        return SchemaType.AVRO;
    }

    @Override
    public SchemaCommon getSchema() {
        HashMap<String, FieldType> dataFields = new HashMap<>();
        for (AvroDataSchemaField field : fields) {
            dataFields.putAll(field.getSchemaField());
        }
        return new SchemaCommon(dataFields);
    }

    @Override
    public Serde<Struct> getSerde() {
        return new AvroSerde(getAvroSchema());
    }

    public Schema getAvroSchema() {
        SchemaBuilder.FieldAssembler<Schema> fieldAssembler = SchemaBuilder.record(name).namespace(namespace).fields();
        for (AvroDataSchemaField field : fields) {
            AvroType fieldType = field.getType();

            switch (fieldType) {
                case BOOLEAN:
                    fieldAssembler = fieldAssembler.name(field.getName()).type().booleanType().noDefault();
                    break;
                case OPTIONAL_BOOLEAN:
                    fieldAssembler = fieldAssembler.name(field.getName()).type().nullable().booleanType().noDefault();
                    break;
                case INT:
                    fieldAssembler = fieldAssembler.name(field.getName()).type().intType().noDefault();
                    break;
                case OPTIONAL_INT:
                    fieldAssembler = fieldAssembler.name(field.getName()).type().nullable().intType().noDefault();
                    break;
                case LONG:
                    fieldAssembler = fieldAssembler.name(field.getName()).type().longType().noDefault();
                    break;
                case OPTIONAL_LONG:
                    fieldAssembler = fieldAssembler.name(field.getName()).type().nullable().longType().noDefault();
                    break;
                case FLOAT:
                    fieldAssembler = fieldAssembler.name(field.getName()).type().floatType().noDefault();
                    break;
                case OPTIONAL_FLOAT:
                    fieldAssembler = fieldAssembler.name(field.getName()).type().nullable().floatType().noDefault();
                    break;
                case STRING:
                    fieldAssembler = fieldAssembler.name(field.getName()).type().stringType().noDefault();
                    break;
                case OPTIONAL_STRING:
                    fieldAssembler = fieldAssembler.name(field.getName()).type().nullable().stringType().noDefault();
                    break;
                case NULL:
                    break;
                default:
                    throw new IllegalArgumentException("Unknown field type: " + fieldType);
            }
        }
        return fieldAssembler.endRecord();
    }
}
