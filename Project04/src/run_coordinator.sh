#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

echo "[+] Running Coordinator Server..."

docker run -d \
  --name my-rmi-coordinator \
  --network project04-network \
  -p 9999:9999 \
  project04-image:latest \
  java -cp . server.MyCoordinatorServer my-rmi-coordinator

echo "[+] Coordinator Server is running with Container ID:"
docker ps -l -q
