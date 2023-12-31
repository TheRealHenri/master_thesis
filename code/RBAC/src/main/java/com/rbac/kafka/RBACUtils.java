package com.rbac.kafka;

public class RBACUtils {


    // local if true; docker if false
    private static final boolean local = false;
    public static final String DB_URL = local ? "jdbc:sqlite:/Users/allgower/Uni/TUB/MA/master_thesis/code/RBACdata/db/rbacdatabase.db" : "jdbc:sqlite:/var/lib/rbac/db/rbacdatabase.db";

    public static final String KAFKA_SERVER = local ? "localhost:9092" : "kafka1:9092,kafka2:9093,kafka3:9094";

}
