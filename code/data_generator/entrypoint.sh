#!/bin/sh

# Create the necessary directories
mkdir -p /project/datasets/single
mkdir -p /project/datasets/multiple
mkdir -p /project/datasets/masked

# Execute the provided command
exec "$@"
