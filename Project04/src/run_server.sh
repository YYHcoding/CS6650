#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

# Usage: ./run_server.sh <server-name> <port>
if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <server-name> <port>"
    exit 1
fi

SERVER_NAME=$1
PORT=$2

echo "[+] Running RMI Server: $SERVER_NAME on port $PORT..."

docker run -d \
  --name $SERVER_NAME \
  --network project04-network \
  -p $PORT:$PORT \
  project04-image:latest \
  java -cp . server.RMIServerApp $SERVER_NAME $PORT my-rmi-coordinator

echo "[+] RMI Server '$SERVER_NAME' is running with Container ID:"
docker ps -l -q
