package com.anonymization.kafka.anonymizers.attributebased.datastructures;

import java.util.HashMap;

public class Node implements Comparable<Node> {

    private final int vertexID;
    private Integer predecessor = null;
    private double distance = Double.POSITIVE_INFINITY;
    private final HashMap<Integer, Edge> adjacencyList = new HashMap<>();

    public Node(int vertexID) {
        this.vertexID = vertexID;
    }

    public Node(int vertexID, double distance) {
        this.vertexID = vertexID;
        this.distance = distance;
    }

    public void addAdjacentNode(Integer nodeId, Edge edge) {
        this.adjacencyList.put(nodeId, edge);
    }

    public int getVertexID() {
        return vertexID;
    }

    public double getDistance() {
        return distance;
    }

    public HashMap<Integer, Edge> getAdjacencyList() {
        return adjacencyList;
    }

    public Integer getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(Integer predecessor) {
        this.predecessor = predecessor;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public int compareTo(Node o) {
        return Double.compare(this.distance, o.distance);
    }
}
