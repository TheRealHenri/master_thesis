import com.dash.anonymizers.attributebased.Aggregation;
import com.dash.anonymizers.attributebased.Shuffling;
import com.dash.anonymizers.attributebased.UnivariateMicroAggregation;
import com.dash.anonymizers.tuplebased.ConditionalSubstitution;
import com.dash.anonymizers.valuebased.*;
import com.dash.configs.stream.Parameter;
import com.dash.configs.stream.ParameterType;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class AnonymizerTests {

    public Struct createTestStruct() {
        Schema schema = SchemaBuilder.struct()
                .field("id", Schema.INT64_SCHEMA)
                .field("name", Schema.STRING_SCHEMA)
                .field("email", Schema.STRING_SCHEMA)
                .field("age", Schema.INT32_SCHEMA)
                .field("salary", Schema.FLOAT64_SCHEMA)
                .field("isActive", Schema.BOOLEAN_SCHEMA)
                .build();

        return new Struct(schema)
                .put("id", 12345L)
                .put("name", "John Doe")
                .put("email", "john.doe@example.com")
                .put("age", 30)
                .put("salary", 55000.0)
                .put("isActive", true);
    }
    public static Struct duplicateAndModifyStruct(Struct original, String field, Object value, Boolean schemaChange) {
        SchemaBuilder schemaBuilder = SchemaBuilder.struct();


        for (Field structField : original.schema().fields()) {
            if (schemaChange && structField.name().equals(field)) {
                schemaBuilder.field(field, Schema.STRING_SCHEMA);
            } else {
                schemaBuilder.field(structField.name(), structField.schema());
            }
        }

        Schema newSchema = schemaBuilder.build();
        Struct duplicated = new Struct(newSchema);


        for (Field structField : original.schema().fields()) {
            Object fieldValue = structField.name().equals(field) ? value : original.get(structField);
            duplicated.put(structField.name(), fieldValue);
        }

        return duplicated;
    }


    @Test
    public void testBlurring() {
        Struct originalStruct = createTestStruct();
        Parameter keysParameter = new Parameter(ParameterType.KEYS, List.of("email"));
        Parameter nFieldsParameter = new Parameter(ParameterType.N_FIELDS, 6);

        Struct expectedAnonymizedStruct = duplicateAndModifyStruct(originalStruct, "email", "john.doe@exampXXXXXX", false);

        Blurring blurring = new Blurring();
        blurring.initialize(List.of(keysParameter, nFieldsParameter));

        List<Struct> result = blurring.anonymize(List.of(originalStruct));
        assertEquals(List.of(expectedAnonymizedStruct), result);

    }

    @Test
    public void testBucketizing() {
        Struct originalStruct = createTestStruct();
        Parameter keysParameter = new Parameter(ParameterType.KEYS, List.of("age"));
        Parameter nBucketsParameter = new Parameter(ParameterType.BUCKET_SIZE, 5);

        Struct expectedAnonymizedStruct = duplicateAndModifyStruct(originalStruct, "age", "[30 - 34]", true);

        Bucketizing bucketizing = new Bucketizing();
        bucketizing.initialize(List.of(keysParameter, nBucketsParameter));

        List<Struct> result = bucketizing.anonymize(List.of(originalStruct));
        assertEquals(List.of(expectedAnonymizedStruct), result);
    }

    @Test
    public void testGeneralization() {
        Struct originalStruct = createTestStruct();
        Parameter keysParameter = new Parameter(ParameterType.KEYS, List.of("email"));
        Parameter generalizationMap = new Parameter(ParameterType.GENERALIZATION_MAP, new HashMap<>(Map.of("john.doe@example.com", "example.com")));

        Struct expectedAnonymizedStruct = duplicateAndModifyStruct(originalStruct, "email", "example.com", false);

        Generalization generalization = new Generalization();
        generalization.initialize(List.of(keysParameter, generalizationMap));

        List<Struct> result = generalization.anonymize(List.of(originalStruct));
        assertEquals(List.of(expectedAnonymizedStruct), result);
    }

    @Test
    public void testNoiseMethods() {
        Struct originalStruct = createTestStruct();
        Parameter keysParameter = new Parameter(ParameterType.KEYS, List.of("salary"));
        Parameter noiseParameter = new Parameter(ParameterType.NOISE, 0.5);
        Parameter seedParameter = new Parameter(ParameterType.SEED, 12345L);

        Struct expectedAnonymizedStruct = duplicateAndModifyStruct(originalStruct, "salary", 47399.17089382595, false);

        NoiseMethods noiseMethods = new NoiseMethods();
        noiseMethods.initialize(List.of(keysParameter, noiseParameter, seedParameter));

        List<Struct> result = noiseMethods.anonymize(List.of(originalStruct));
        assertEquals(List.of(expectedAnonymizedStruct), result);
    }

    @Test
    public void testSubstitution() {
        Struct originalStruct = createTestStruct();
        Parameter keysParameter = new Parameter(ParameterType.KEYS, List.of("name"));
        Parameter substitutionMap = new Parameter(ParameterType.SUBSTITUTION_LIST, List.of("Jane Doe"));

        Struct expectedAnonymizedStruct = duplicateAndModifyStruct(originalStruct, "name", "Jane Doe", false);

        Substitution substitution = new Substitution();
        substitution.initialize(List.of(keysParameter, substitutionMap));

        List<Struct> result = substitution.anonymize(List.of(originalStruct));
        assertEquals(List.of(expectedAnonymizedStruct), result);
    }

    @Test
    public void testSuppression() {
        Struct originalStruct = createTestStruct();
        Parameter keysParameter = new Parameter(ParameterType.KEYS, List.of("salary"));

        Struct expectedAnonymizedStruct = duplicateAndModifyStruct(originalStruct, "salary", "*", true);

        Suppression suppression = new Suppression();
        suppression.initialize(List.of(keysParameter));

        List<Struct> result = suppression.anonymize(List.of(originalStruct));
        assertEquals(List.of(expectedAnonymizedStruct), result);
    }

    @Test
    public void testConditionalMatchSubstitution() {
        Struct originalStruct = createTestStruct();
        Parameter keysParameter = new Parameter(ParameterType.KEYS, List.of("name"));
        Parameter conditionParameter = new Parameter(ParameterType.CONDITION_MAP, new HashMap<>(Map.of("matchValue", "John Doe")));
        Parameter substitutionMap = new Parameter(ParameterType.SUBSTITUTION_LIST, List.of("Jane Doe"));

        Struct expectedAnonymizedStruct = duplicateAndModifyStruct(originalStruct, "name", "Jane Doe", false);

        ConditionalSubstitution conditionalSubstitution = new ConditionalSubstitution();
        conditionalSubstitution.initialize(List.of(keysParameter, substitutionMap, conditionParameter));

        List<Struct> result = conditionalSubstitution.anonymize(List.of(originalStruct));
        assertEquals(List.of(expectedAnonymizedStruct), result);
    }

    @Test
    public void testConditionalRangeSubstitution() {
        Struct originalStruct = createTestStruct();
        Parameter keysParameter = new Parameter(ParameterType.KEYS, List.of("salary"));
        ArrayList<Number> range = new ArrayList<>();
        range.add(50000);
        range.add(60000);
        Parameter conditionParameter = new Parameter(ParameterType.CONDITION_MAP, new HashMap<>(Map.of("matchRange", range)));
        Parameter substitutionMap = new Parameter(ParameterType.SUBSTITUTION_LIST, List.of("XXXXXX"));

        Struct expectedAnonymizedStruct = duplicateAndModifyStruct(originalStruct, "salary", "XXXXXX", true);

        ConditionalSubstitution conditionalSubstitution = new ConditionalSubstitution();
        conditionalSubstitution.initialize(List.of(keysParameter, substitutionMap, conditionParameter));

        List<Struct> result = conditionalSubstitution.anonymize(List.of(originalStruct));
        assertEquals(List.of(expectedAnonymizedStruct), result);
    }

    @Test
    public void testConditionalRegexSubstitution() {
        Struct originalStruct = createTestStruct();
        Parameter keysParameter = new Parameter(ParameterType.KEYS, List.of("email"));
        Parameter conditionParameter = new Parameter(ParameterType.CONDITION_MAP, new HashMap<>(Map.of("matchRegex", "^[a-zA-Z0-9._%+-]+@example\\.com$")));
        Parameter substitutionMap = new Parameter(ParameterType.SUBSTITUTION_LIST, List.of("XXXXXX"));

        Struct expectedAnonymizedStruct = duplicateAndModifyStruct(originalStruct, "email", "XXXXXX", true);

        ConditionalSubstitution conditionalSubstitution = new ConditionalSubstitution();
        conditionalSubstitution.initialize(List.of(keysParameter, substitutionMap, conditionParameter));

        List<Struct> result = conditionalSubstitution.anonymize(List.of(originalStruct));
        assertEquals(List.of(expectedAnonymizedStruct), result);
    }

    @Test
    public void testMedianAggregation() {
        String field = "salary";
        List<Struct> originalStructs = new ArrayList<>();
        Struct originalStruct = createTestStruct();

        Parameter keysParameter = new Parameter(ParameterType.KEYS, List.of(field));
        Parameter aggregationParameter = new Parameter(ParameterType.AGGREGATION_MODE, "median");
        Parameter windowSizeParameter = new Parameter(ParameterType.WINDOW_SIZE, 100);

        for (int i = 0; i < 100; i++) {
            originalStructs.add(duplicateAndModifyStruct(originalStruct, field, originalStruct.getFloat64(field) + i * 100, false));
        }

        Struct expectedAnonymizedStruct = duplicateAndModifyStruct(originalStruct, field, 59950.0, false);
        List<Struct> expectedAnonymizedStructs = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            expectedAnonymizedStructs.add(expectedAnonymizedStruct);
        }

        Aggregation aggregation = new Aggregation();
        aggregation.initialize(List.of(keysParameter, aggregationParameter, windowSizeParameter));

        List<Struct> result = aggregation.anonymize(originalStructs);
        assertEquals(expectedAnonymizedStructs, result);
    }

    @Test
    public void testSumAggregation() {
        String field = "age";
        List<Struct> originalStructs = new ArrayList<>();
        Struct originalStruct = createTestStruct();

        Parameter keysParameter = new Parameter(ParameterType.KEYS, List.of(field));
        Parameter aggregationParameter = new Parameter(ParameterType.AGGREGATION_MODE, "sum");
        Parameter windowSizeParameter = new Parameter(ParameterType.WINDOW_SIZE, 100);

        for (int i = 0; i < 100; i++) {
            originalStructs.add(originalStruct);
        }

        Struct expectedAnonymizedStruct = duplicateAndModifyStruct(originalStruct, field, 30 * 100, false);
        List<Struct> expectedAnonymizedStructs = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            expectedAnonymizedStructs.add(expectedAnonymizedStruct);
        }

        Aggregation aggregation = new Aggregation();
        aggregation.initialize(List.of(keysParameter, aggregationParameter, windowSizeParameter));

        List<Struct> result = aggregation.anonymize(originalStructs);
        assertEquals(expectedAnonymizedStructs, result);
    }

    @Test
    public void testAverageAggregation() {
        String field = "salary";
        List<Struct> originalStructs = new ArrayList<>();
        Struct originalStruct = createTestStruct();

        Parameter keysParameter = new Parameter(ParameterType.KEYS, List.of(field));
        Parameter aggregationParameter = new Parameter(ParameterType.AGGREGATION_MODE, "average");
        Parameter windowSizeParameter = new Parameter(ParameterType.WINDOW_SIZE, 100);

        for (int i = 0; i < 100; i++) {
            originalStructs.add(duplicateAndModifyStruct(originalStruct, field, originalStruct.getFloat64(field) + i * 100, false));
        }

        Struct expectedAnonymizedStruct = duplicateAndModifyStruct(originalStruct, field, 59950.0, false);
        List<Struct> expectedAnonymizedStructs = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            expectedAnonymizedStructs.add(expectedAnonymizedStruct);
        }

        Aggregation aggregation = new Aggregation();
        aggregation.initialize(List.of(keysParameter, aggregationParameter, windowSizeParameter));

        List<Struct> result = aggregation.anonymize(originalStructs);
        assertEquals(expectedAnonymizedStructs, result);
    }

    @Test
    public void testMinAggregation() {
        String field = "salary";
        List<Struct> originalStructs = new ArrayList<>();
        Struct originalStruct = createTestStruct();

        Parameter keysParameter = new Parameter(ParameterType.KEYS, List.of(field));
        Parameter aggregationParameter = new Parameter(ParameterType.AGGREGATION_MODE, "min");
        Parameter windowSizeParameter = new Parameter(ParameterType.WINDOW_SIZE, 100);

        for (int i = 0; i < 100; i++) {
            originalStructs.add(duplicateAndModifyStruct(originalStruct, field, originalStruct.getFloat64(field) + i * 100, false));
        }

        Struct expectedAnonymizedStruct = duplicateAndModifyStruct(originalStruct, field, 55000.0, false);
        List<Struct> expectedAnonymizedStructs = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            expectedAnonymizedStructs.add(expectedAnonymizedStruct);
        }

        Aggregation aggregation = new Aggregation();
        aggregation.initialize(List.of(keysParameter, aggregationParameter, windowSizeParameter));

        List<Struct> result = aggregation.anonymize(originalStructs);
        assertEquals(expectedAnonymizedStructs, result);
    }

    @Test
    public void testMaxAggregation() {
        String field = "salary";
        List<Struct> originalStructs = new ArrayList<>();
        Struct originalStruct = createTestStruct();

        Parameter keysParameter = new Parameter(ParameterType.KEYS, List.of(field));
        Parameter aggregationParameter = new Parameter(ParameterType.AGGREGATION_MODE, "max");
        Parameter windowSizeParameter = new Parameter(ParameterType.WINDOW_SIZE, 100);

        for (int i = 0; i < 100; i++) {
            originalStructs.add(duplicateAndModifyStruct(originalStruct, field, originalStruct.getFloat64(field) + i * 100, false));
        }

        Struct expectedAnonymizedStruct = duplicateAndModifyStruct(originalStruct, field, 55000.0 + 99 * 100, false);
        List<Struct> expectedAnonymizedStructs = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            expectedAnonymizedStructs.add(expectedAnonymizedStruct);
        }

        Aggregation aggregation = new Aggregation();
        aggregation.initialize(List.of(keysParameter, aggregationParameter, windowSizeParameter));

        List<Struct> result = aggregation.anonymize(originalStructs);
        assertEquals(expectedAnonymizedStructs, result);
    }

    @Test
    public void testShuffing() {
        String field = "name";
        Struct originalStruct1 = createTestStruct();
        Struct originalStruct2 = duplicateAndModifyStruct(originalStruct1, field, "Max Fosh", false);
        Struct originalStruct3 = duplicateAndModifyStruct(originalStruct1, field, "Summer Anne", false);
        Struct originalStruct4 = duplicateAndModifyStruct(originalStruct1, field, "Lex Luthor", false);
        Struct originalStruct5 = duplicateAndModifyStruct(originalStruct1, field, "Steve", false);

        List<Struct> originalStructs = List.of(originalStruct1, originalStruct2, originalStruct3, originalStruct4, originalStruct5);

        Parameter keysParameter = new Parameter(ParameterType.KEYS, List.of(field));
        Parameter seedParameter = new Parameter(ParameterType.SEED, 1L);
        Parameter windowSizeParameter = new Parameter(ParameterType.WINDOW_SIZE, 2);

        Struct expectedAnonymizedStruct1 = duplicateAndModifyStruct(originalStruct1, field, "Summer Anne", false);
        Struct expectedAnonymizedStruct2 = duplicateAndModifyStruct(originalStruct2, field, "Lex Luthor", false);
        Struct expectedAnonymizedStruct3 = duplicateAndModifyStruct(originalStruct3, field, "Max Fosh", false);
        Struct expectedAnonymizedStruct4 = duplicateAndModifyStruct(originalStruct4, field, "Steve", false);
        Struct expectedAnonymizedStruct5 = duplicateAndModifyStruct(originalStruct5, field, "John Doe", false);


        List<Struct> expectedAnonymizedStructs = List.of(expectedAnonymizedStruct1, expectedAnonymizedStruct2, expectedAnonymizedStruct3, expectedAnonymizedStruct4, expectedAnonymizedStruct5);

        Shuffling shuffling = new Shuffling();
        shuffling.initialize(List.of(keysParameter, seedParameter, windowSizeParameter));

        List<Struct> result = shuffling.anonymize(originalStructs);
        assertEquals(expectedAnonymizedStructs, result);
    }

    @Test
    public void testUnivariateMicroAggregation() {
        String field = "age";
        Struct originalStruct = createTestStruct();

        Struct struct1 = duplicateAndModifyStruct(originalStruct, field, 31, false);
        Struct struct2 = duplicateAndModifyStruct(originalStruct, field, 30, false);
        Struct struct3 = duplicateAndModifyStruct(originalStruct, field, 29, false);
        Struct struct4 = duplicateAndModifyStruct(originalStruct, field, 46, false);
        Struct struct5 = duplicateAndModifyStruct(originalStruct, field, 45, false);
        Struct struct6 = duplicateAndModifyStruct(originalStruct, field, 44, false);
        Struct struct7 = duplicateAndModifyStruct(originalStruct, field, 59, false);
        Struct struct8 = duplicateAndModifyStruct(originalStruct, field, 60, false);
        Struct struct9 = duplicateAndModifyStruct(originalStruct, field, 61, false);

        List<Struct> originalStructs = new ArrayList<>(List.of(struct1, struct2, struct3, struct4, struct5, struct6, struct7, struct8, struct9));

        Struct expectedAnonymizedStruct1 = duplicateAndModifyStruct(originalStruct, field, 30, false);
        Struct expectedAnonymizedStruct2 = duplicateAndModifyStruct(originalStruct, field, 45, false);
        Struct expectedAnonymizedStruct3 = duplicateAndModifyStruct(originalStruct, field, 60, false);

        List<Struct> expectedAnonymizedStructs = new ArrayList<>(List.of(expectedAnonymizedStruct1, expectedAnonymizedStruct1, expectedAnonymizedStruct1,
                expectedAnonymizedStruct2, expectedAnonymizedStruct2, expectedAnonymizedStruct2,
                expectedAnonymizedStruct3, expectedAnonymizedStruct3, expectedAnonymizedStruct3));

        Parameter keysParameter = new Parameter(ParameterType.KEYS, List.of(field));
        Parameter kParameter = new Parameter(ParameterType.K, 3);
        Parameter windowSizeParameter = new Parameter(ParameterType.WINDOW_SIZE, 9);


        UnivariateMicroAggregation univariateMicroAggregation = new UnivariateMicroAggregation();
        univariateMicroAggregation.initialize(List.of(keysParameter, kParameter, windowSizeParameter));

        List<Struct> result = univariateMicroAggregation.anonymize(originalStructs);
        assertEquals(expectedAnonymizedStructs, result);
    }
}
