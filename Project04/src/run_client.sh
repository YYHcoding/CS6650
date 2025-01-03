#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

# Usage: ./run_client.sh <coordinator-host> <coordinator-port> [--batch]
if [ "$#" -lt 2 ] || [ "$#" -gt 3 ]; then
    echo "Usage: $0 <coordinator-host> <coordinator-port> [--batch]"
    exit 1
fi

COORDINATOR_HOST=$1
PORT=$2
BATCH_MODE=$3

echo "[+] Running Client connected to $COORDINATOR_HOST on port $PORT..."

if [ "$BATCH_MODE" == "--batch" ]; then
    docker run -it \
      --name paxos-client \
      --network project04-network \
      project04-image:latest \
      java -cp . client.RMIClientApp $COORDINATOR_HOST $PORT --batch
else
    docker run -it \
      --name paxos-client \
      --network project04-network \
      project04-image:latest \
      java -cp . client.RMIClientApp $COORDINATOR_HOST $PORT
fi
