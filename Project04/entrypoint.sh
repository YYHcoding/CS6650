#!/bin/bash
set -e

if [ "$#" -lt 1 ]; then
    echo "Usage: $0 <component> [<args>...]"
    echo "Components:"
    echo "  coordinator"
    echo "  server <server-name> <port>"
    echo "  client <coordinator-host> <coordinator-port>"
    exit 1
fi

component=$1
shift

case "$component" in
    coordinator)
        echo "[+] Starting Coordinator Server..."
        exec java -cp . server.MyCoordinatorServer "$@"
        ;;
    server)
        if [ "$#" -ne 2 ]; then
            echo "Usage: $0 server <server-name> <port>"
            exit 1
        fi
        server_name=$1
        port=$2
        echo "[+] Starting RMI Server: $server_name on port $port..."
        exec java -cp . server.RMIServer "$server_name" "$port" "$COORDINATOR_HOST"
        ;;
    client)
        if [ "$#" -ne 2 ]; then
            echo "Usage: $0 client <coordinator-host> <coordinator-port>"
            exit 1
        fi
        coordinator_host=$1
        coordinator_port=$2
        echo "[+] Starting Client connected to $coordinator_host on port $coordinator_port..."
        exec java -cp . client.RMIClientApp "$coordinator_host" "$coordinator_port"
        ;;
    *)
        echo "Unknown component: $component"
        echo "Components:"
        echo "  coordinator"
        echo "  server <server-name> <port>"
        echo "  client <coordinator-host> <coordinator-port>"
        exit 1
        ;;
esac
