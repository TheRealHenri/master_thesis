package com.dash.anonymizers.tablebased.datastructures;

import org.apache.kafka.connect.data.Struct;

public class DataPoint {
    private final int position;
    private Struct data;

    public DataPoint(int position, Struct data) {
        this.position = position;
        this.data = data;
    }

    public int getPosition() {
        return position;
    }

    public Struct getData() {
        return data;
    }

    public void setData(Struct data) {
        this.data = data;
    }
}
