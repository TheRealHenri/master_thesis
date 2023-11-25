package com.rbac.kafka;

import com.beust.jcommander.JCommander;

import java.util.Scanner;

public class RBACBootstrapper {
    public static void main(String[] args) {

        if (args.length < 1 || !args[0].equals("cli")) {
            System.out.println("Running in idle mode. Run './rbac_cli.sh' in the root directory to start CLI.");
            while (true) {
                try {
                    Thread.sleep(Long.MAX_VALUE); // Sleep indefinitely
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Interrupted, exiting idle mode.");
                    break;
                }
            }
            return;
        }

        System.out.println("Initializing RBAC");
        System.out.println("Setting up Kafka");
        KafkaController kafkaController = new KafkaController();
        kafkaController.initializeKafkaAdmin();
        DatabaseManager databaseManager = new DatabaseManager(kafkaController);
        System.out.println("Setting up Database");
        System.out.println("Ready to go!");
        System.out.println("Starting CLI");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ready for command!");
        System.out.println("Type 'exit' to exit");
        System.out.println("Type 'help' to see available commands");

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();
            if ("exit".equalsIgnoreCase(line)) {
                kafkaController.closeKafkaAdmin();
                break;
            }

            String[] input = line.split(" ");
            handleInput(input, databaseManager);
        }
    }

    public static void handleInput(String[] line, DatabaseManager databaseManager) {
        CLICommands commands = new CLICommands(databaseManager);
        JCommander jCommander = JCommander.newBuilder()
                .addObject(commands)
                .build();

        try {
            jCommander.parse(line);
            commands.runCommand();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}