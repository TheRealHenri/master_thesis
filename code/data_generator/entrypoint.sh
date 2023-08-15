#!/bin/sh

# Create the necessary directories
mkdir -p /project/datasets/single
mkdir -p /project/datasets/multiple

# Execute the provided command
exec "$@"
