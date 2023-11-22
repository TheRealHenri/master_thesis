#!/bin/bash

TOPIC="dash_commands"
HOST_SERVER="localhost"
PORT="9094"
BOOTSTRAP_SERVER=$HOST_SERVER:$PORT

if [ "$#" -lt 1 ]; then
    echo "Usage: ./streamManager.sh <command> [args...]"
    echo "Use the help command for a list of available commands"
    exit 1
fi

if [ "$1" = "help" ]; then
    echo "Available commands:"
    echo "  initialize            - Initialize streams"
    echo "  start_all_streams     - Start all streams"
    echo "  stop_all_streams      - Stop all streams"
    echo "  pause_all_streams     - Pause all streams"
    echo "  start_stream <name>   - Start a specific stream"
    echo "  stop_stream <name>    - Stop a specific stream"
    echo "  pause_stream <name>   - Pause a specific stream"
    echo "  list_streams          - List all streams"
    echo "  close                 - Shutdown system"
    exit 0
fi

nc -z -w2 $HOST_SERVER $PORT > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "Unable to connect to Kafka on $BOOTSTRAP_SERVER. Please check your Kafka server."
    exit 1
fi

echo "$1 $2" | docker compose exec -T kafka /opt/bitnami/kafka/bin/kafka-console-producer.sh --topic "$TOPIC" --bootstrap-server "$BOOTSTRAP_SERVER"

if [ $? -ne 0 ]; then
    echo "Failed to send the command to Kafka. Please check the system for any issues."
    exit 1
fi

