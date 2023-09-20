package com.anonymization.kafka.anonymizers.attributebased;

import com.anonymization.kafka.anonymizers.WindowConfig;
import com.anonymization.kafka.anonymizers.attributebased.datastructures.Edge;
import com.anonymization.kafka.anonymizers.attributebased.datastructures.Node;
import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.configs.stream.ParameterType;
import com.anonymization.kafka.validators.KeyValidator;
import com.anonymization.kafka.validators.ParameterExpectation;
import com.anonymization.kafka.validators.PositiveIntegerValidator;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.kafka.connect.data.Values.convertToDouble;

public class UnivariateMicroAggregation implements AttributeBasedAnonymizer {

    private List<String> keysToAggregate = Collections.emptyList();
    private Duration windowSize = Duration.ZERO;
    private Optional<Duration> advanceTime = Optional.empty();
    private Optional<Duration> gracePeriod = Optional.empty();
    private int k = 0;
    private final Logger log = LoggerFactory.getLogger(UnivariateMicroAggregation.class);

    @Override
    public List<Struct> anonymize(List<Struct> lineS) {
        Schema schema = lineS.get(0).schema().field(keysToAggregate.get(0)).schema();
        List<Object> values = lineS.stream().map(struct -> struct.get(keysToAggregate.get(0))).collect(Collectors.toList());

        List<Double> attributeList = new ArrayList<>();
        for (Object value : values) {
            attributeList.add(convertToDouble(schema, value));
        }

        List<Map.Entry<Integer, Double>> indexedList =
                IntStream.range(0, attributeList.size())
                        .boxed()
                        .map(i -> new AbstractMap.SimpleEntry<>(i, attributeList.get(i)))
                        .sorted(Map.Entry.comparingByValue())
                        .collect(Collectors.toList());

        List<Double> sortedValues = indexedList.stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        List<Integer> originalIndices = indexedList.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (k >= sortedValues.size()) {
            log.warn("k is greater than the number of elements in the window. Returning the mean of the window.");
            double average = sortedValues.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            for (Struct struct : lineS) {
                struct.put(keysToAggregate.get(0), convertBackToSchema(average, schema));
            }
            return lineS;
        }

        // construct graph G(k;n)
        List<Node> nodes = new ArrayList<>();
        for (int i = 1; i <= sortedValues.size(); i++) {
            nodes.add(new Node(i));
        }
        // adding source node
        nodes.add(0, new Node(0, 0));
        for (int i = 0; i < sortedValues.size() + 1; i++) {
            for (int j = i + k; j < i + 2 * k && j < sortedValues.size() + 1; j++) {
                List<Integer> correspondingGroupIndices = originalIndices.subList(i, j);
                List<Double> correspondingGroup = sortedValues.subList(i, j);
                double sum = correspondingGroup.stream().mapToDouble(Double::doubleValue).sum();
                double mean = sum / correspondingGroup.size();
                double squaredError = 0;
                for (double value : correspondingGroup) {
                    squaredError += Math.pow(value - mean, 2);
                }
                Edge edge = new Edge(j, squaredError, mean, correspondingGroupIndices);
                nodes.get(i).addAdjacentNode(j, edge);
            }
        }

        // Dijkstra's algorithm
        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(nodes.get(0));
        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();
            for (Map.Entry<Integer, Edge> adjacentNode : currentNode.getAdjacencyList().entrySet()) {
                Node node = nodes.get(adjacentNode.getKey());
                double newDistance = currentNode.getDistance() + adjacentNode.getValue().getWeight();
                if (newDistance < node.getDistance()) {
                    queue.remove(node);
                    node.setDistance(newDistance);
                    node.setPredecessor(currentNode.getVertexID());
                    queue.add(node);
                }
            }
        }

        Node currentNode = nodes.get(nodes.size() - 1);
        List<Edge> resultingEdges = new ArrayList<>();
        while (true) {
            int currentIndex = currentNode.getVertexID();
            Integer predecessorIndex = currentNode.getPredecessor();
            if (predecessorIndex == null) {
                break;
            } else {
                currentNode = nodes.get(predecessorIndex);
                resultingEdges.add(currentNode.getAdjacencyList().get(currentIndex));
            }
        }

        for (Edge edge : resultingEdges) {
            for (int index : edge.getCorrespondingGroupIndices()) {
                Struct struct = lineS.get(index);
                struct.put(keysToAggregate.get(0), convertBackToSchema(edge.getValue(), schema));
            }
        }

        return lineS;
    }

    private Object convertBackToSchema(double value, Schema schema) {
        switch (schema.type()) {
            case INT32:
                return (int) value;
            case INT64:
                return (long) value;
            case FLOAT32:
                return (float) value;
            case FLOAT64:
                return value;
            default:
                throw new IllegalArgumentException("Schema type " + schema.type() + " is not supported.");
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
                ),
                new ParameterExpectation(
                        ParameterType.K.getName(),
                        List.of(new PositiveIntegerValidator()),
                        true
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
                case WINDOW_SIZE:
                    this.windowSize = Duration.ofMillis(param.getWindowSize());
                    break;
                case ADVANCE_TIME:
                    this.advanceTime = Optional.of(Duration.ofMillis(param.getAdvanceTime()));
                    break;
                case GRACE_PERIOD:
                    this.gracePeriod = Optional.of(Duration.ofMillis(param.getGracePeriod()));
                case K:
                    this.k = param.getK();
                    break;
            }
        }
        if (keysToAggregate.size() != 1) {
            throw new IllegalArgumentException("Univariate Microaggregation is microaggregation on one attribute. For more attributes use Multivariate Microaggregation.");
        }
    }

    public UnivariateMicroAggregation() {
    }

    @Override
    public WindowConfig getWindowConfig() {
        return new WindowConfig(windowSize, advanceTime, gracePeriod);
    }
}
