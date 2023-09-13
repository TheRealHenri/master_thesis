package com.anonymization.kafka.anonymizers.attributebased;

import com.anonymization.kafka.anonymizers.WindowConfig;
import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.configs.stream.ParameterType;
import com.anonymization.kafka.validators.EnumValidator;
import com.anonymization.kafka.validators.KeyValidator;
import com.anonymization.kafka.validators.ParameterExpectation;
import com.anonymization.kafka.validators.PositiveIntegerValidator;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Aggregation implements AttributeBasedAnonymizer {

    private List<String> keysToAggregate = Collections.emptyList();
    private AggregationMode mode;
    private Duration windowSize = Duration.ZERO;
    private Optional<Duration> advanceTime = Optional.empty();
    private Optional<Duration> gracePeriod = Optional.empty();
    private final Logger log = LoggerFactory.getLogger(Aggregation.class);

    @Override
    public List<Struct> anonymize(List<Struct> lineS) {
        if (lineS.isEmpty()) {
            log.info("Window contains no data");
            return Collections.emptyList();
        }
        log.info("Size " + mode + " before anonymization: " + lineS.size());

        Struct sampleStruct = lineS.get(0);

        for (String key : keysToAggregate) {
            for (Field field : sampleStruct.schema().fields()) {
                if (field.name().equals(key)) {
                    Schema schema = field.schema();
                    switch (mode) {
                        case SUM:
                            sumForSchemaType(lineS, key, schema);
                            break;
                        case MEDIAN:
                            medianForSchemaType(lineS, key, schema);
                            break;
                        case AVERAGE:
                            averageForSchemaType(lineS, key, schema);
                            break;
                        case MAX:
                            maxForSchemaType(lineS, key, schema);
                            break;
                        case MIN:
                            minForSchemaType(lineS, key, schema);
                            break;
                        case COUNT:
                            countForSchemaType(lineS, key, schema);
                            break;
                        case MODE:
                            modeForSchemaType(lineS, key, schema);
                            break;
                    }
                }
            }
        }
        log.info("Size " + mode + " after anonymization: " + lineS.size());
        return lineS;
    }

    private void sumForSchemaType(List<Struct> structs, String key, Schema schema) {
        if (schema.equals(Schema.OPTIONAL_INT32_SCHEMA) || schema.equals(Schema.INT32_SCHEMA)) {
            int sum = structs.stream().mapToInt(struct -> struct.getInt32(key)).sum();
            structs.forEach(struct -> struct.put(key, sum));
        } else if (schema.equals(Schema.OPTIONAL_INT64_SCHEMA) || schema.equals(Schema.INT64_SCHEMA)) {
            long sum = structs.stream().mapToLong(struct -> struct.getInt64(key)).sum();
            structs.forEach(struct -> struct.put(key, sum));
        } else if (schema.equals(Schema.OPTIONAL_FLOAT32_SCHEMA) || schema.equals(Schema.FLOAT32_SCHEMA)) {
            float sum = (float) structs.stream().mapToDouble(struct -> struct.getFloat32(key)).sum();
            structs.forEach(struct -> struct.put(key, sum));
        } else if (schema.equals(Schema.OPTIONAL_FLOAT64_SCHEMA) || schema.equals(Schema.FLOAT64_SCHEMA)) {
            double sum = structs.stream().mapToDouble(struct -> struct.getFloat64(key)).sum();
            structs.forEach(struct -> struct.put(key, sum));
        } else {
            throw new IllegalArgumentException("Aggregation can only be applied to number types e.g. int, long, float or double. Does not work for provided Type: " + schema);
        }
    }

    private void medianForSchemaType(List<Struct> structs, String key, Schema schema) {
        if (schema.equals(Schema.OPTIONAL_INT32_SCHEMA) || schema.equals(Schema.INT32_SCHEMA)) {
            List<Integer> sortedIntList = structs.stream().map(struct -> struct.getInt32(key)).sorted().collect(Collectors.toList());
            int median;
            if (sortedIntList.size() % 2 == 0) {
                median = (sortedIntList.get(sortedIntList.size() / 2 - 1) + sortedIntList.get(sortedIntList.size() / 2)) / 2;
            } else {
                median = sortedIntList.get(sortedIntList.size() / 2);
            }
            structs.forEach(struct -> struct.put(key, median));
        } else if (schema.equals(Schema.OPTIONAL_INT64_SCHEMA) || schema.equals(Schema.INT64_SCHEMA)) {
            List<Long> sortedLongList = structs.stream().map(struct -> struct.getInt64(key)).sorted().collect(Collectors.toList());
            long median;
            if (sortedLongList.size() % 2 == 0) {
                median = (sortedLongList.get(sortedLongList.size() / 2 - 1) + sortedLongList.get(sortedLongList.size() / 2)) / 2;
            } else {
                median = sortedLongList.get(sortedLongList.size() / 2);
            }
            structs.forEach(struct -> struct.put(key, median));
        } else if (schema.equals(Schema.OPTIONAL_FLOAT32_SCHEMA) || schema.equals(Schema.FLOAT32_SCHEMA)) {
            List<Float> sortedFloatList = structs.stream().map(struct -> struct.getFloat32(key)).sorted().collect(Collectors.toList());
            float median;
            if (sortedFloatList.size() % 2 == 0) {
                median = (sortedFloatList.get(sortedFloatList.size() / 2 - 1) + sortedFloatList.get(sortedFloatList.size() / 2)) / 2;
            } else {
                median = sortedFloatList.get(sortedFloatList.size() / 2);
            }
            structs.forEach(struct -> struct.put(key, median));
        } else if (schema.equals(Schema.OPTIONAL_FLOAT64_SCHEMA) || schema.equals(Schema.FLOAT64_SCHEMA)) {
            List<Double> sortedDoubleList = structs.stream().map(struct -> struct.getFloat64(key)).sorted().collect(Collectors.toList());
            double median;
            if (sortedDoubleList.size() % 2 == 0) {
                median = (sortedDoubleList.get(sortedDoubleList.size() / 2 - 1) + sortedDoubleList.get(sortedDoubleList.size() / 2)) / 2;
            } else {
                median = sortedDoubleList.get(sortedDoubleList.size() / 2);
            }
            structs.forEach(struct -> struct.put(key, median));
        } else {
            throw new IllegalArgumentException("Aggregation can only be applied to number types e.g. int, long, float or double. Does not work for provided Type: " + schema);
        }
    }

    private void averageForSchemaType(List<Struct> structs, String key, Schema schema) {
        if (schema.equals(Schema.OPTIONAL_INT32_SCHEMA) || schema.equals(Schema.INT32_SCHEMA)) {
            double average = structs.stream().mapToInt(struct -> struct.getInt32(key)).average().orElse(0);
            structs.forEach(struct -> struct.put(key, (int) Math.round(average)));
        } else if (schema.equals(Schema.OPTIONAL_INT64_SCHEMA) || schema.equals(Schema.INT64_SCHEMA)) {
            double average = structs.stream().mapToLong(struct -> struct.getInt64(key)).average().orElse(0);
            structs.forEach(struct -> struct.put(key, Math.round(average)));
        } else if (schema.equals(Schema.OPTIONAL_FLOAT32_SCHEMA) || schema.equals(Schema.FLOAT32_SCHEMA)) {
            double average = (float) structs.stream().mapToDouble(struct -> struct.getFloat32(key)).average().orElse(0);
            structs.forEach(struct -> struct.put(key, (float) average));
        } else if (schema.equals(Schema.OPTIONAL_FLOAT64_SCHEMA) || schema.equals(Schema.FLOAT64_SCHEMA)) {
            double average = structs.stream().mapToDouble(struct -> struct.getFloat64(key)).average().orElse(0);
            structs.forEach(struct -> struct.put(key, average));
        } else {
            throw new IllegalArgumentException("Aggregation can only be applied to number types e.g. int, long, float or double. Does not work for provided Type: " + schema);
        }
    }

    private void maxForSchemaType(List<Struct> structs, String key, Schema schema) {
        if (schema.equals(Schema.OPTIONAL_INT32_SCHEMA) || schema.equals(Schema.INT32_SCHEMA)) {
            int max = structs.stream().mapToInt(struct -> struct.getInt32(key)).max().orElse(0);
            structs.forEach(struct -> struct.put(key, max));
        } else if (schema.equals(Schema.OPTIONAL_INT64_SCHEMA) || schema.equals(Schema.INT64_SCHEMA)) {
            long max = structs.stream().mapToLong(struct -> struct.getInt64(key)).max().orElse(0L);
            structs.forEach(struct -> struct.put(key, max));
        } else if (schema.equals(Schema.OPTIONAL_FLOAT32_SCHEMA) || schema.equals(Schema.FLOAT32_SCHEMA)) {
            float max = (float) structs.stream().mapToDouble(struct -> struct.getFloat32(key)).max().orElse(0);
            structs.forEach(struct -> struct.put(key, max));
        } else if (schema.equals(Schema.OPTIONAL_FLOAT64_SCHEMA) || schema.equals(Schema.FLOAT64_SCHEMA)) {
            double max = structs.stream().mapToDouble(struct -> struct.getFloat64(key)).max().orElse(0);
            structs.forEach(struct -> struct.put(key, max));
        } else {
            throw new IllegalArgumentException("Aggregation can only be applied to number types e.g. int, long, float or double. Does not work for provided Type: " + schema);
        }
    }

    private void minForSchemaType(List<Struct> structs, String key, Schema schema) {
        if (schema.equals(Schema.OPTIONAL_INT32_SCHEMA) || schema.equals(Schema.INT32_SCHEMA)) {
            int min = structs.stream().mapToInt(struct -> struct.getInt32(key)).min().orElse(0);
            structs.forEach(struct -> struct.put(key, min));
        } else if (schema.equals(Schema.OPTIONAL_INT64_SCHEMA) || schema.equals(Schema.INT64_SCHEMA)) {
            long min = structs.stream().mapToLong(struct -> struct.getInt64(key)).min().orElse(0L);
            structs.forEach(struct -> struct.put(key, min));
        } else if (schema.equals(Schema.OPTIONAL_FLOAT32_SCHEMA) || schema.equals(Schema.FLOAT32_SCHEMA)) {
            float min = (float) structs.stream().mapToDouble(struct -> struct.getFloat32(key)).min().orElse(0);
            structs.forEach(struct -> struct.put(key, min));
        } else if (schema.equals(Schema.OPTIONAL_FLOAT64_SCHEMA) || schema.equals(Schema.FLOAT64_SCHEMA)) {
            double min = structs.stream().mapToDouble(struct -> struct.getFloat64(key)).min().orElse(0);
            structs.forEach(struct -> struct.put(key, min));
        } else {
            throw new IllegalArgumentException("Aggregation can only be applied to number types e.g. int, long, float or double. Does not work for provided Type: " + schema);
        }
    }

    private void countForSchemaType(List<Struct> structs, String key, Schema schema) {
        if (schema.equals(Schema.OPTIONAL_INT32_SCHEMA) || schema.equals(Schema.INT32_SCHEMA)) {
            int count = (int) structs.stream().mapToInt(struct -> struct.getInt32(key)).count();
            structs.forEach(struct -> struct.put(key, count));
        } else if (schema.equals(Schema.OPTIONAL_INT64_SCHEMA) || schema.equals(Schema.INT64_SCHEMA)) {
            long count = structs.stream().mapToLong(struct -> struct.getInt64(key)).count();
            structs.forEach(struct -> struct.put(key, count));
        } else if (schema.equals(Schema.OPTIONAL_FLOAT32_SCHEMA) || schema.equals(Schema.FLOAT32_SCHEMA)) {
            float count = (float) structs.stream().mapToDouble(struct -> struct.getFloat32(key)).count();
            structs.forEach(struct -> struct.put(key, count));
        } else if (schema.equals(Schema.OPTIONAL_FLOAT64_SCHEMA) || schema.equals(Schema.FLOAT64_SCHEMA)) {
            double count = (double) structs.stream().mapToDouble(struct -> struct.getFloat64(key)).count();
            structs.forEach(struct -> struct.put(key, count));
        } else {
            throw new IllegalArgumentException("Aggregation can only be applied to number types e.g. int, long, float or double. Does not work for provided Type: " + schema);
        }
    }

    private void modeForSchemaType(List<Struct> structs, String key, Schema schema) {
        if (schema.equals(Schema.OPTIONAL_INT32_SCHEMA) || schema.equals(Schema.INT32_SCHEMA)) {
            int mode = structs.stream()
                    .collect(Collectors.groupingBy(struct -> struct.getInt32(key), Collectors.counting()))
                    .entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(0);
            structs.forEach(struct -> struct.put(key, mode));
        } else if (schema.equals(Schema.OPTIONAL_INT64_SCHEMA) || schema.equals(Schema.INT64_SCHEMA)) {
            long mode = structs.stream()
                    .collect(Collectors.groupingBy(struct -> struct.getInt64(key), Collectors.counting()))
                    .entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(0L);
            structs.forEach(struct -> struct.put(key, mode));
        } else if (schema.equals(Schema.OPTIONAL_FLOAT32_SCHEMA) || schema.equals(Schema.FLOAT32_SCHEMA)) {
            float mode = structs.stream()
                    .collect(Collectors.groupingBy(struct -> struct.getFloat32(key), Collectors.counting()))
                    .entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse((float) 0);
            structs.forEach(struct -> struct.put(key, mode));
        } else if (schema.equals(Schema.OPTIONAL_FLOAT64_SCHEMA) || schema.equals(Schema.FLOAT64_SCHEMA)) {
            double mode = structs.stream()
                    .collect(Collectors.groupingBy(struct -> struct.getFloat64(key), Collectors.counting()))
                    .entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse((double) 0);
            structs.forEach(struct -> struct.put(key, mode));
        } else {
            throw new IllegalArgumentException("Aggregation can only be applied to number types e.g. int, long, float or double. Does not work for provided Type: " + schema);
        }
    }

    @Override
    public List<ParameterExpectation> getParameterExpectations() {
        return List.of(
                new ParameterExpectation(
                        ParameterType.KEYS.getName(),
                        List.of(new KeyValidator(true)),
                        true
                ),
                new ParameterExpectation(
                        ParameterType.AGGREGATION_MODE.getName(),
                        List.of(new EnumValidator(AggregationMode.class)),
                        true
                ),
                new ParameterExpectation(
                        ParameterType.WINDOW_SIZE.getName(),
                        List.of(new PositiveIntegerValidator()),
                        true
                ),
                new ParameterExpectation(
                        ParameterType.ADVANCE_TIME.getName(),
                        List.of(new PositiveIntegerValidator()),
                        false
                ),
                new ParameterExpectation(
                        ParameterType.GRACE_PERIOD.getName(),
                        List.of(new PositiveIntegerValidator()),
                        false
                )
        );
    }

    @Override
    public void initialize(List<Parameter> parameters) {
        for (Parameter param : parameters) {
            switch (param.getType()) {
                case KEYS:
                    this.keysToAggregate = param.getKeys();
                    break;
                case AGGREGATION_MODE:
                    this.mode = AggregationMode.getByName(param.getAggregationMode());
                    break;
                case WINDOW_SIZE:
                    this.windowSize = Duration.ofMillis(param.getWindowSize());
                    break;
                case ADVANCE_TIME:
                    this.advanceTime = Optional.of(Duration.ofMillis(param.getAdvanceTime()));
                    break;
                case GRACE_PERIOD:
                    this.gracePeriod = Optional.of(Duration.ofMillis(param.getGracePeriod()));
                    break;
            }
        }
    }

    public Aggregation() {
    }

    @Override
    public WindowConfig getWindowConfig() {
        return new WindowConfig(windowSize, advanceTime, gracePeriod);
    }
}
