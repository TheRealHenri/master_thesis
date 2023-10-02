package com.anonymization.kafka.anonymizers.tablebased;

import com.anonymization.kafka.anonymizers.WindowConfig;
import com.anonymization.kafka.anonymizers.tablebased.datastructures.*;
import com.anonymization.kafka.configs.stream.Parameter;
import com.anonymization.kafka.configs.stream.ParameterType;
import com.anonymization.kafka.configs.stream.QuasiIdentifier;
import com.anonymization.kafka.validators.KeyValidator;
import com.anonymization.kafka.validators.ParameterExpectation;
import com.anonymization.kafka.validators.PositiveIntegerValidator;
import com.anonymization.kafka.validators.QIKeysValidator;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class KAnonymization implements TableBasedAnonymizer {

    private List<String> keysToSuppress = Collections.emptyList();
    private int k = 0;
    private int delta = 0;
    private int mu = 0;
    private int beta = 0;
    private final HashMap<String, GeneralizationHierarchy> qisHierarchyMap = new HashMap<>();

    private static final HashSet<Cluster> gamma = new HashSet<>();
    private static final HashSet<Cluster> omega = new HashSet<>();
    private static RollingAverage tau;
    private final static TreeMap<Integer, DataPointClusterPair> deltaConstraintMap = new TreeMap<>();
    private final Logger log = LoggerFactory.getLogger(KAnonymization.class);

    @Override
    public List<Struct> anonymize(List<Struct> lineS, int position) {
        if (lineS.isEmpty()) {
            return Collections.emptyList();
        }
        List<DataPoint> ejectedDataPoints = new ArrayList<>();
        for (Struct line : lineS) {
            DataPoint dataPoint = new DataPoint(position, line);
            dataPoint.setData(adjustSchema(line));
            Cluster cluster = bestSelection(dataPoint);
            if (cluster == null) {
                cluster = new Cluster(dataPoint, getKeyGeneralizationMapFor(dataPoint.getData()));
                gamma.add(cluster);
            } else {
                cluster.addDataPoint(dataPoint);
            }
            deltaConstraintMap.put(position, new DataPointClusterPair(dataPoint, cluster));
            Map.Entry<Integer, DataPointClusterPair> firstEntry = deltaConstraintMap.firstEntry();
            if (firstEntry != null && firstEntry.getKey() < position - delta) {
                ejectedDataPoints.addAll(deltaConstraint(firstEntry.getValue()));
            }
        }
        updateDeltaConstraint(ejectedDataPoints);
        suppressIdentifiableKeys(ejectedDataPoints);
        return convertDataPointsToStructs(ejectedDataPoints);
    }

    @Override
    public List<Struct> anonymize(List<Struct> lineS) {
        log.error("This anonymizer does not support windowed anonymization");
        return lineS;
    }

    private Cluster bestSelection(DataPoint dataPoint) {
        HashMap<Cluster, Double> clusterEnlargementCostMap = new HashMap<>();
        for (Cluster cluster : gamma) {
            clusterEnlargementCostMap.put(cluster, cluster.getEnlargementCostFor(dataPoint));
        }
        Double minimum = Math.min(clusterEnlargementCostMap.values().stream().min(Double::compareTo).orElse(0.0), 0.0);
        HashSet<Cluster> minEnlargementClusters = clusterEnlargementCostMap.keySet().stream().filter(cluster -> clusterEnlargementCostMap.get(cluster).equals(minimum)).collect(HashSet::new, HashSet::add, HashSet::addAll);

        HashSet<Cluster> bestClusters = new HashSet<>();
        for (Cluster cluster : minEnlargementClusters) {
            double informationLoss = cluster.getInformationLossIncluding(dataPoint);
            if (informationLoss <= tau.get()) {
                bestClusters.add(cluster);
            }
        }

        if (bestClusters.isEmpty()) {
            if (gamma.size() >= beta) {
                return minEnlargementClusters.stream().findAny().orElse(null);
            } else {
                return null;
            }
        }
        return bestClusters.stream().findAny().orElse(null);
    }

    private List<DataPoint> deltaConstraint(DataPointClusterPair expiredDataPointClusterPair) {
        DataPoint expiredDataPoint = expiredDataPointClusterPair.getDataPoint();
        Cluster associatedCluster = expiredDataPointClusterPair.getCluster();
        if (associatedCluster.getSize() >= k) {
            return outputCluster(associatedCluster);
        } else {
            HashSet<Cluster> ksAnonymizedClustersContainingT = new HashSet<>();
            for (Cluster cluster : omega) {
                if (cluster.fitsGeneralizationFor(expiredDataPoint)) {
                    ksAnonymizedClustersContainingT.add(cluster);
                }
            }
            if (!ksAnonymizedClustersContainingT.isEmpty()) {
                Cluster randomCLusterContainingT = ksAnonymizedClustersContainingT.stream().findAny().orElse(null);
                return List.of(randomCLusterContainingT.generalize(expiredDataPoint));
            }
            int m = 0;
            for (Cluster cluster : gamma) {
                if (associatedCluster.getSize() < cluster.getSize()) {
                    m++;
                }
            }
            int n = gamma.size();
            if (m > 0.5 * n) {
                suppress(expiredDataPoint);
                return List.of(expiredDataPoint);
            }
            int dataPointsInGamma = gamma.stream().mapToInt(Cluster::getSize).sum();
            if (dataPointsInGamma < k) {
                suppress(expiredDataPoint);
                return List.of(expiredDataPoint);
            }
            Cluster mergedCluster = mergeClusters(associatedCluster);
            return outputCluster(mergedCluster);
        }
    }

    public List<DataPoint> outputCluster(Cluster cluster) {
        List<DataPoint> output = new ArrayList<>();
        HashSet<Cluster> clusterSubset = new HashSet<>();
        if (cluster.getSize() >= 2 * k) {
            clusterSubset = splitCluster(cluster);
        } else {
            clusterSubset.add(cluster);
        }
        for (Cluster outputCluster : clusterSubset) {
            output.addAll(outputCluster.getGeneralizedDataPoints());
            double informationLoss = outputCluster.getCurrentInformationLoss();
            tau.addValue(informationLoss);
            if (informationLoss < tau.get()) {
                omega.add(outputCluster);
            }
            gamma.remove(outputCluster);
        }

        return output;
    }

    public HashSet<Cluster> splitCluster(Cluster cluster) {
        List<DataPoint> dataPoints = cluster.getDataPoints();
        HashSet<Cluster> clusterSubset = new HashSet<>();
        while (dataPoints.size() >= k) {
            DataPoint randomDataPoint = dataPoints.stream().findAny().orElse(null);
            if (randomDataPoint == null) break;
            HashMap<Integer, List<DataPoint>> dataPointsById = new HashMap<>();
            for (DataPoint dataPoint : dataPoints) {
                dataPointsById.computeIfAbsent(getIdForDataPoint(dataPoint), k -> new ArrayList<>()).add(dataPoint);
            }
            Cluster clusterForRandomDataPoint = new Cluster(randomDataPoint, getKeyGeneralizationMapFor(randomDataPoint.getData()));
            TreeMap<Double, DataPoint> enlargementCostMap = new TreeMap<>();
            for (Map.Entry<Integer, List<DataPoint>> entry : dataPointsById.entrySet()) {
                DataPoint currentDataPoint = entry.getValue().stream().findAny().orElse(null);
                if (Objects.equals(getIdForDataPoint(randomDataPoint), entry.getKey()) || currentDataPoint == null) {
                    continue;
                }
                double enlargementCost = clusterForRandomDataPoint.getEnlargementCostFor(currentDataPoint);
                enlargementCostMap.put(enlargementCost, currentDataPoint);
            }
            if (dataPointsById.size() > k - 1) {
                for (int i = 0; i < k - 1; i++) {
                    Map.Entry<Double, DataPoint> firstEntry = enlargementCostMap.pollFirstEntry();
                    clusterForRandomDataPoint.addDataPoint(firstEntry.getValue());
                    dataPoints.remove(firstEntry.getValue());
                }
                clusterSubset.add(clusterForRandomDataPoint);
            } else {
                break;
            }
        }
        HashMap<Integer, List<DataPoint>> dataPointsById = new HashMap<>();
        for (DataPoint dataPoint : dataPoints) {
            dataPointsById.computeIfAbsent(getIdForDataPoint(dataPoint), k -> new ArrayList<>()).add(dataPoint);
        }
        for (Map.Entry<Integer, List<DataPoint>> entry : dataPointsById.entrySet()) {
            TreeMap<Double, Cluster> enlargementCostMap = new TreeMap<>();
            DataPoint currentDataPoint = entry.getValue().stream().findAny().orElse(null);
            if (currentDataPoint == null) {
                continue;
            }
            for (Cluster remainingCluster : clusterSubset) {
                double enlargementCost = remainingCluster.getEnlargementCostFor(currentDataPoint);
                enlargementCostMap.put(enlargementCost, remainingCluster);
            }
            Map.Entry<Double, Cluster> firstEntry = enlargementCostMap.pollFirstEntry();
            for (DataPoint dataPoint : entry.getValue()) {
                firstEntry.getValue().addDataPoint(dataPoint);
            }
        }
        return clusterSubset;
    }

    public Integer getIdForDataPoint(DataPoint dataPoint) {
        return Integer.parseInt(dataPoint.getData().getString("id"));
    }

    public Struct adjustSchema(Struct dataPoint) {
        SchemaBuilder schemaBuilder = SchemaBuilder.struct();

        for (Field field : dataPoint.schema().fields()) {
            if (keysToSuppress.contains(field.name()) || qisHierarchyMap.containsKey(field.name())) {
                if (field.schema().equals(Schema.STRING_SCHEMA) || field.schema().equals(Schema.OPTIONAL_STRING_SCHEMA)) {
                    schemaBuilder.field(field.name(), field.schema());
                } else {
                    schemaBuilder.field(field.name(), Schema.STRING_SCHEMA);
                }
            } else {
                schemaBuilder.field(field.name(), field.schema());
            }
        }

        Schema newSchema = schemaBuilder.build();
        Struct newStruct = new Struct(newSchema);

        for (Field field : dataPoint.schema().fields()) {
            if (keysToSuppress.contains(field.name()) || qisHierarchyMap.containsKey(field.name())) {
                newStruct.put(field.name(), dataPoint.get(field).toString());
            } else {
                newStruct.put(field.name(), dataPoint.get(field));
            }
        }

        return newStruct;
    }

    public void suppress(DataPoint dataPoint) {
        for (String key : qisHierarchyMap.keySet()) {
            dataPoint.getData().put(key, "*");
        }
    }

    public Cluster mergeClusters(Cluster associatedCluster) {
        gamma.remove(associatedCluster);
        TreeMap<Double, Cluster> clusterEnlargementMap = new TreeMap<>();
        for (Cluster cluster : gamma) {
            clusterEnlargementMap.put(associatedCluster.getEnlargementCostFor(cluster), cluster);
        }
        while (associatedCluster.getSize() < k) {
            Map.Entry<Double, Cluster> lowestEnlargementEntry = clusterEnlargementMap.pollFirstEntry();
            associatedCluster.absorbCluster(lowestEnlargementEntry.getValue());
            gamma.remove(lowestEnlargementEntry.getValue());
        }
        gamma.add(associatedCluster);
        return associatedCluster;
    }

    private HashMap<String, Generalization> getKeyGeneralizationMapFor(Struct dataPoint) {
        HashMap<String, Generalization> keyGeneralizationMap = new HashMap<>();
        for (String key : qisHierarchyMap.keySet()) {
            GeneralizationHierarchy hierarchy = qisHierarchyMap.get(key);
            Generalization generalization;
            if (hierarchy instanceof NumericalHierarchy) {
                NumericalHierarchy numericalHierarchy = (NumericalHierarchy) hierarchy;
                generalization = new NumericalGeneralization(numericalHierarchy.getBucketSize(), numericalHierarchy.getRangeStart(), numericalHierarchy.getRangeEnd());
                generalization.generalize(dataPoint.get(key).toString());
            } else if (hierarchy instanceof CategoricalHierarchy) {
                CategoricalHierarchy categoricalHierarchy = (CategoricalHierarchy) hierarchy;
                HashMap<String, CategoricalHierarchy> leafMap = getLeafMapFor(categoricalHierarchy);
                generalization = new CategoricalGeneralization(categoricalHierarchy.getNumberOfLeafNodes(), leafMap, leafMap.get(dataPoint.get(key).toString()));
            } else {
                throw new RuntimeException("Unknown hierarchy type");
            }
            keyGeneralizationMap.put(key, generalization);
        }
        return keyGeneralizationMap;
    }

    private HashMap<String, CategoricalHierarchy> getLeafMapFor(CategoricalHierarchy rootNode) {
        HashMap<String, CategoricalHierarchy> leafMap = new HashMap<>();
        findLeaves(rootNode, leafMap);
        return leafMap;
    }

    private void findLeaves(CategoricalHierarchy node, HashMap<String, CategoricalHierarchy> leafMap) {
        if (node.getChildren().isEmpty()) {
            leafMap.put(node.getValue(), node);
            return;
        }
        for (CategoricalHierarchy child : node.getChildren()) {
            findLeaves(child, leafMap);
        }
    }

    private void updateDeltaConstraint(List<DataPoint> output) {
        for (DataPoint dataPoint : output) {
            deltaConstraintMap.remove(dataPoint.getPosition());
        }
    }

    private void suppressIdentifiableKeys(List<DataPoint> output) {
        for (String key : keysToSuppress) {
            for (DataPoint dataPoint : output) {
                dataPoint.getData().put(key, "*");
            }
        }
    }

    private List<Struct> convertDataPointsToStructs(List<DataPoint> dataPoints) {
        return dataPoints.stream().map(DataPoint::getData).collect(Collectors.toList());
    }

    @Override
    public List<ParameterExpectation> getParameterExpectations() {
        return List.of(
                new ParameterExpectation(
                        ParameterType.KEYS.getName(),
                        List.of(new KeyValidator()),
                        false
                ),
                new ParameterExpectation(
                        ParameterType.K.getName(),
                        List.of(new PositiveIntegerValidator()),
                        true
                ),
                new ParameterExpectation(
                        ParameterType.DELTA.getName(),
                        List.of(new PositiveIntegerValidator()),
                        true
                ),
                new ParameterExpectation(
                        ParameterType.MU.getName(),
                        List.of(new PositiveIntegerValidator()),
                        true
                ),
                new ParameterExpectation(
                        ParameterType.BETA.getName(),
                        List.of(new PositiveIntegerValidator()),
                        true
                ),
                new ParameterExpectation(
                        ParameterType.QUASI_IDENTIFIERS.getName(),
                        List.of(new QIKeysValidator()),
                        true
                )
        );
    }

    @Override
    public void initialize(List<Parameter> parameters) {
        for (Parameter param : parameters) {
            switch (param.getType()) {
                case KEYS:
                    this.keysToSuppress = param.getKeys();
                    break;
                case K:
                    this.k = param.toInt();
                    break;
                case DELTA:
                    this.delta = param.getDelta();
                    break;
                case MU:
                    this.mu = param.getMu();
                    break;
                case BETA:
                    this.beta = param.getBeta();
                    break;
                case QUASI_IDENTIFIERS:
                    for (QuasiIdentifier qi : param.getQuasiIdentifiers()) {
                        qisHierarchyMap.put(qi.getKey(), qi.getHierarchy());
                    }
                    break;
            }
        }
        tau = new RollingAverage(mu);
    }

    public KAnonymization() {}

    @Override
    public WindowConfig getWindowConfig() {
        return null;
    }
}
