package com.anonymization.kafka.anonymizers.tablebased.datastructures;

import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Cluster {
    private final ArrayList<Struct> dataPoints = new ArrayList<>();
    private final HashMap<String, Generalization> keyGeneralizationMap;
    private final Logger log = LoggerFactory.getLogger(Cluster.class);
    public Cluster(Struct dataPoint, HashMap<String, Generalization> keyGeneralizationMap) {
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

    public boolean fitsGeneralizationFor(Struct dataPoint) {
        boolean fitsGeneralization = false;
        for (Map.Entry<String, Generalization> entry : keyGeneralizationMap.entrySet()) {
            String value = dataPoint.get(entry.getKey()).toString();
            fitsGeneralization = entry.getValue().fitsGeneralization(value);
            if (!fitsGeneralization) {
                return false;
            }
        }
        return fitsGeneralization;
    }

    public double getEnlargementCostFor(Struct dataPoint) {
        double enlargementCost = 0;
        for (Map.Entry<String, Generalization> entry : keyGeneralizationMap.entrySet()) {
            String value = dataPoint.get(entry.getKey()).toString();
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

    public double getInformationLossIncluding(Struct dataPoint) {
        double informationLoss = 0;
        for (Map.Entry<String, Generalization> entry : keyGeneralizationMap.entrySet()) {
            String value = dataPoint.get(entry.getKey()).toString();
            informationLoss += entry.getValue().getInformationLossIncluding(value);
        }
        return informationLoss;
    }

    public void addDataPoint(Struct dataPoint) {
        dataPoints.add(dataPoint);
        for (Map.Entry<String, Generalization> entry : keyGeneralizationMap.entrySet()) {
            String value = dataPoint.get(entry.getKey()).toString();
            entry.getValue().generalize(value);
        }
    }

    public int getSize() {
        return dataPoints.size();
    }

    public ArrayList<Struct> getDataPoints() {
        return dataPoints;
    }

    public HashMap<String, Generalization> getKeyGeneralizationMap() {
        return keyGeneralizationMap;
    }

    public Struct generalize(Struct dataPoint) {
        for (Map.Entry<String, Generalization> entry : keyGeneralizationMap.entrySet()) {
            String generalizedValue = entry.getValue().getGeneralization();
            dataPoint.put(entry.getKey(), generalizedValue);
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

    public List<Struct> getGeneralizedDataPoints() {
        for (Map.Entry<String, Generalization> entry : keyGeneralizationMap.entrySet()) {
            dataPoints.forEach(datapoint -> datapoint.put(entry.getKey(), entry.getValue().getGeneralization()));
        }
        return dataPoints;
    }
}
