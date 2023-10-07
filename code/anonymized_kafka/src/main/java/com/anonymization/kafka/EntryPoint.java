package com.anonymization.kafka;

public class EntryPoint {
    public static void main(String[] args) {
        StreamManager manager = StreamManager.getInstance();

        if (args.length < 1) {
            System.out.println("Please provide a valid command.");
            return;
        }

        switch (args[0]) {
            case "help":
                System.out.println("Available commands:");
                System.out.println("  initialize            - Initialize streams");
                System.out.println("  start_all_streams     - Start all streams");
                System.out.println("  stop_all_streams      - Stop all streams");
                System.out.println("  pause_all_streams     - Pause all streams");
                System.out.println("  start_stream <name>   - Start a specific stream");
                System.out.println("  stop_stream <name>    - Stop a specific stream");
                System.out.println("  pause_stream <name>   - Pause a specific stream");
                System.out.println("  list_streams          - List all streams");
                System.out.println("  close                 - Shutdown system");
                break;
            case "initialize":
                manager.initializeStreams();
                break;
            case "start_all_streams":
                manager.startAllStreams();
                break;
            case "stop_all_streams":
                manager.stopAllStreams();
                break;
            case "pause_all_streams":
                manager.pauseAllStreams();
                break;
            case "start_stream":
                if (args.length > 1) {
                    manager.startStream(args[1]);
                } else {
                    System.out.println("Please provide a stream name.");
                }
                break;
            case "stop_stream":
                if (args.length > 1) {
                    manager.stopStream(args[1]);
                } else {
                    System.out.println("Please provide a stream name.");
                }
                break;
            case "pause_stream":
                if (args.length > 1) {
                    manager.pauseStream(args[1]);
                } else {
                    System.out.println("Please provide a stream name.");
                }
                break;
            case "list_streams":
                manager.listStreams();
                break;
            case "close":
                manager.close();
                break;
            default:
                System.out.println("Unknown command.");
                break;
        }

    }
}
