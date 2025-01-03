Overview
Project04 is a Java-based distributed Key-Value store that ensures fault tolerance using the Paxos consensus algorithm. The system leverages Java RMI (Remote Method Invocation) for communication between clients and servers and uses Docker for containerization, facilitating easy deployment and scalability.

Prerequisites
Before you begin, ensure you have the following installed on your machine:

Docker: Download Docker
Git: Download Git
Java JDK 11: Download JDK 11
IntelliJ IDEA (Recommended): Download IntelliJ IDEA
Setup Instructions

1. Clone the Repository
   Start by cloning the project repository to your local machine:

cd src

2. Build the Docker Image
   Use the provided deploy.sh script to build the Docker image.


./deploy.sh
Note: Ensure the script has execute permissions. If not, run:


chmod +x deploy.sh
3. Create Docker Network
   All containers need to communicate over a common network. Create a Docker network:


docker network create project04-network
4. Start the Coordinator Server
   Run the Coordinator Server using the run_coordinator.sh script.



./run_coordinator.sh
Script Overview (run_coordinator.sh):

Starts the Coordinator Server container named my-rmi-coordinator on port 9999.
Connects the container to the project04-network.
Ensure the script has execute permissions:


chmod +x run_coordinator.sh
5. Start RMI Servers
   Launch multiple RMI Servers to enable fault tolerance. Use the run_server.sh script for each server.


./run_server.sh server1 1231
./run_server.sh server2 1232
./run_server.sh server3 1233
./run_server.sh server4 1234
./run_server.sh server5 1235
Script Overview (run_server.sh):

Starts an RMI Server container with a unique name (e.g., server1) on a specified port (e.g., 1231).
Connects the container to the project04-network.
Registers the server with the Coordinator.
Ensure the script has execute permissions:


chmod +x run_server.sh
6. Run the Client
   The client can operate in two modes:

Batch Mode: Automatically performs 5 PUTs, 5 GETs, and 5 DELETEs.
Interactive Mode: Allows manual input of commands.
a. Batch Mode
Run the client in batch mode using the --batch flag.


./run_client.sh my-rmi-coordinator 9999 --batch
b. Interactive Mode
Run the client without the --batch flag to enter interactive mode.

./run_client.sh my-rmi-coordinator 9999
Script Overview (run_client.sh):

Starts the Client container named paxos-client.
Connects to the Coordinator at my-rmi-coordinator on port 9999.
Optionally runs in batch mode if --batch is provided.
Ensure the script has execute permissions:

chmod +x run_client.sh
Example Interactive Commands:

Command: PUT key1 value1
Response: true
Command: GET key1
Response: value1
Command: DELETE key1
Response: true
Command: QUIT
Connection to server closed!e: https://www.baeldung.com/java-synchronized