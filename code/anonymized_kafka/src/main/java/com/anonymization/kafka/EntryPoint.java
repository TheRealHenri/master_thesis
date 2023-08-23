package com.anonymization.kafka;

import com.anonymization.kafka.StreamManager;

public class EntryPoint {
    public static void main(String[] args) {
        StreamManager manager = StreamManager.getInstance();

        if (args.length < 1) {
            System.out.println("Please provide a valid command.");
            return;
        }

        switch (args[0]) {
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
            default:
                System.out.println("Unknown command.");
                break;
        }

    }
}
