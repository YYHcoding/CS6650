#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

echo "[+] Building Docker image for Project04..."

# Build the Docker image without cache
docker build --no-cache -t project04-image:latest -f Dockerfile .

echo "[+] Docker image 'project04-image:latest' built successfully."
