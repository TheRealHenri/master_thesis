package com.rbac.kafka;

import com.rbac.kafka.repository.DatabaseManager;

public class RBACBootstrapper {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Compiling RBAC!");
            return;
        }

        System.out.println("Initializing RBAC");
        DatabaseManager databaseManager = new DatabaseManager();
        System.out.println("Setting up Database");
        System.out.println("Setting up Kafka");
        System.out.println("Ready to go!");
    }
}