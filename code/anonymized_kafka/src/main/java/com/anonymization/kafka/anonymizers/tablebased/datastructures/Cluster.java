package com.anonymization.kafka.anonymizers.tablebased.datastructures;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cluster {
    private final ArrayList<DataPoint> dataPoints = new ArrayList<>();
    private final HashMap<String, Generalization> keyGeneralizationMap;
    private final Logger log = LoggerFactory.getLogger(Cluster.class);
    public Cluster(DataPoint dataPoint, HashMap<String, Generalization> keyGeneralizationMap) {
        this.dataPoints.add(dataPoint);
        this.keyGeneralizationMap = keyGeneralizationMap;
    }

    public double getCurrentInformationLoss() {
        double informationLoss = 0;
        for (Generalization generalization : keyGeneralizationMap.values()) {
            informationLoss += generalization.getCurrentInformationLoss();
        }
        return informationLoss;
    }

    public boolean fitsGeneralizationFor(DataPoint dataPoint) {
        boolean fitsGeneralization = false;
        for (Map.Entry<String, Generalization> entry : keyGeneralizationMap.entrySet()) {
            String value = dataPoint.getData().get(entry.getKey()).toString();
            fitsGeneralization = entry.getValue().fitsGeneralization(value);
            if (!fitsGeneralization) {
                return false;
            }
        }
        return fitsGeneralization;
    }

    public double getEnlargementCostFor(DataPoint dataPoint) {
        double enlargementCost = 0;
        for (Map.Entry<String, Generalization> entry : keyGeneralizationMap.entrySet()) {
            String value = dataPoint.getData().get(entry.getKey()).toString();
            enlargementCost += entry.getValue().calculateEnlargementCost(value);
        }
        return enlargementCost;
    }

    public double getEnlargementCostFor(Cluster cluster) {
        double enlargementCost = 0;
        for (Map.Entry<String, Generalization> remainingClusterEntry : keyGeneralizationMap.entrySet()) {
            Generalization potentialGeneralization = cluster.getKeyGeneralizationMap().get(remainingClusterEntry.getKey());
            enlargementCost += remainingClusterEntry.getValue().calculateEnlargementCost(potentialGeneralization);
        }
        return enlargementCost;
    }

    public double getInformationLossIncluding(DataPoint dataPoint) {
        double informationLoss = 0;
        for (Map.Entry<String, Generalization> entry : keyGeneralizationMap.entrySet()) {
            String value = dataPoint.getData().get(entry.getKey()).toString();
            informationLoss += entry.getValue().getInformationLossIncluding(value);
        }
        return informationLoss;
    }

    public void addDataPoint(DataPoint dataPoint) {
        dataPoints.add(dataPoint);
        for (Map.Entry<String, Generalization> entry : keyGeneralizationMap.entrySet()) {
            String value = dataPoint.getData().get(entry.getKey()).toString();
            entry.getValue().generalize(value);
        }
    }

    public int getSize() {
        return dataPoints.size();
    }

    public ArrayList<DataPoint> getDataPoints() {
        return dataPoints;
    }

    public HashMap<String, Generalization> getKeyGeneralizationMap() {
        return keyGeneralizationMap;
    }

    public DataPoint generalize(DataPoint dataPoint) {
        for (Map.Entry<String, Generalization> entry : keyGeneralizationMap.entrySet()) {
            String generalizedValue = entry.getValue().getGeneralization();
            dataPoint.getData().put(entry.getKey(), generalizedValue);
        }
        return dataPoint;
    }

    public void absorbCluster(Cluster cluster) {
        dataPoints.addAll(cluster.getDataPoints());
        for (Map.Entry<String, Generalization> remainingClusterEntry : keyGeneralizationMap.entrySet()) {
            Generalization absorbedGeneralization = cluster.getKeyGeneralizationMap().get(remainingClusterEntry.getKey());
            remainingClusterEntry.getValue().mergeGeneralization(absorbedGeneralization);
        }
    }

    public List<DataPoint> getGeneralizedDataPoints() {
        for (Map.Entry<String, Generalization> entry : keyGeneralizationMap.entrySet()) {
            dataPoints.forEach(datapoint -> datapoint.getData().put(entry.getKey(), entry.getValue().getGeneralization()));
        }
        return dataPoints;
    }
}
