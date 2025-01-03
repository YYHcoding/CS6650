// File: src/client/RMIClient.java
package client;

import server.CoordinatorServer;
import server.ServerHeader;
import util.KeyValueDB;
import util.Log;

import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * RMI implementation of Client.
 * Supports both interactive and batch (automated) modes.
 */
public class RMIClient implements Client {
    protected final Scanner scanner;
    protected static final int SERVER_TIMEOUT = 20000; // in milliseconds (= 20 sec)
    private final String coordinatorHost;
    private final int coordinatorPort;
    private static boolean connected;
    private static KeyValueDB db;
    private final boolean batchMode;

    /**
     * Constructor for RMIClient
     *
     * @param coordinatorHost hostname of the Coordinator RMI registry
     * @param coordinatorPort port of the Coordinator RMI registry
     * @param batchMode        flag to indicate batch mode
     * @throws RemoteException
     * @throws NotBoundException
     */
    public RMIClient(String coordinatorHost, int coordinatorPort, boolean batchMode) throws RemoteException, NotBoundException {
        this.coordinatorHost = coordinatorHost;
        this.coordinatorPort = coordinatorPort;
        this.batchMode = batchMode;
        scanner = new Scanner(System.in);
        connected = false;

        // Connect to Coordinator Server
        Registry coordinatorRegistry = LocateRegistry.getRegistry(this.coordinatorHost, this.coordinatorPort);
        CoordinatorServer coordinatorServer = (CoordinatorServer) coordinatorRegistry.lookup("ServerListService");

        // Retrieve list of available RMI Servers
        List<ServerHeader> serverHeaders = coordinatorServer.getServerList();

        if (serverHeaders == null || serverHeaders.isEmpty()) {
            throw new RemoteException("No available RMI Servers found.");
        }

        // Select an RMI Server from the list
        ServerHeader selectedServer = selectServer(serverHeaders);

        // Connect to the selected RMI Server's KeyValueDBService
        try {
            Registry serverRegistry = LocateRegistry.getRegistry(selectedServer.getHost(), selectedServer.getPort());
            db = (KeyValueDB) serverRegistry.lookup("KeyValueDBService");
            connected = true;
            Log.logln(String.format("Connected to RMI Server at %s:%d", selectedServer.getHost(), selectedServer.getPort()));
        } catch (Exception e) {
            Log.logln("Failed to connect to the selected RMI Server: " + e.getMessage());
            throw new RemoteException("Failed to connect to RMI Server.", e);
        }
    }

    /**
     * Selects an RMI Server from the list.
     * Currently selects a random server. This can be enhanced to use more sophisticated selection logic.
     *
     * @param serverHeaders List of available RMI Servers
     * @return Selected ServerHeader
     */
    private ServerHeader selectServer(List<ServerHeader> serverHeaders) {
        Random random = new Random();
        ServerHeader selectedServer = serverHeaders.get(random.nextInt(serverHeaders.size()));
        Log.logln("Selected RMI Server: " + selectedServer.getHost() + ":" + selectedServer.getPort());
        return selectedServer;
    }

    @Override
    public void execute() throws RemoteException {
        if (batchMode) {
            performBatchOperations();
        } else {
            performInteractiveOperations();
        }
    }

    /**
     * Performs interactive operations allowing user to input commands.
     */
    private void performInteractiveOperations() {
        try {
            Log.logln("Database Content (Key, Value):\n" + db.getString());
        } catch (RemoteException e) {
            Log.logln("Error fetching database content: " + e.getMessage());
        }

        Log.logln("Possible commands: PUT/GET/DELETE/QUIT\n");
        String input = "";
        try {
            while (connected) {
                // Taking client request
                Log.log("Command: ");
                input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("QUIT")) {
                    break;
                }
                // Process the request
                String response = processRequest(input);
                Log.logln("Response: " + response);
            }
        } catch (Exception e) {
            // Empty catch for Ctrl+C or unexpected errors
            Log.logln("Client encountered an error: " + e.getMessage());
        }
        Log.logln("Connection to server closed!");
    }

    /**
     * Performs batch operations: 5 PUTs, 5 GETs, and 5 DELETEs.
     */
    private void performBatchOperations() {
        Log.logln("Starting batch operations: 5 PUTs, 5 GETs, 5 DELETEs.");

        // Predefined set of keys and values
        String[] keys = {"NAME", "GENDER", "ID", "CLASS", "GRADE"};
        String[] values = {"YANHUI YANG", "MALE", "002249965", "CS6650", "A"};

        // Perform 5 PUT operations
        Log.logln("Performing 5 PUT operations...");
        for (int i = 0; i < keys.length; i++) {
            String command = String.format("PUT %s %s", keys[i], values[i]);
            String response = processRequest(command);
            Log.logln(String.format("Command: %s | Response: %s", command, response));
        }

        // Perform 5 GET operations
        Log.logln("Performing 5 GET operations...");
        for (String key : keys) {
            String command = String.format("GET %s", key);
            String response = processRequest(command);
            Log.logln(String.format("Command: %s | Response: %s", command, response));
        }

        // Perform 5 DELETE operations
        Log.logln("Performing 5 DELETE operations...");
        for (String key : keys) {
            String command = String.format("DELETE %s", key);
            String response = processRequest(command);
            Log.logln(String.format("Command: %s | Response: %s", command, response));
        }

        Log.logln("Batch operations completed.");
        Log.logln("Connection to server closed!");
    }

    /**
     * Processes a single command input by the user or from batch operations.
     *
     * @param input Command string
     * @return Response string
     */
    private String processRequest(String input) {
        if (input.equalsIgnoreCase("ALL")) {
            // Gives list of all key-value pairs in the database
            try {
                return "Database Content (Key, Value):\n" + db.getString();
            } catch (RemoteException e) {
                return "Error fetching database content.";
            }
        }

        String[] data = input.split("\\s+");
        if (data.length == 0) {
            return "No command entered.";
        }

        String command = data[0].toUpperCase();

        switch (command) {
            case "PUT":
                if (checkPut(data)) {
                    String key = data[1];
                    String value = data[2];
                    return executeCommand(() -> db.put(key, value));
                } else {
                    // Improper arguments after PUT
                    return "Invalid format for PUT. Expected: PUT <key> <value>";
                }
            case "GET":
                if (checkGet(data)) {
                    String key = data[1];
                    return executeCommand(() -> db.get(key));
                } else {
                    // Improper arguments after GET
                    return "Invalid format for GET. Expected: GET <key>";
                }
            case "DELETE":
                if (checkDelete(data)) {
                    String key = data[1];
                    return executeCommand(() -> db.delete(key));
                } else {
                    // Improper arguments after DELETE
                    return "Invalid format for DELETE. Expected: DELETE <key>";
                }
            default:
                // Improper request from client or command not recognized
                return "Received malformed request or unknown command!";
        }
    }

    /**
     * Executes a command action and handles RemoteException.
     *
     * @param action CommandAction functional interface
     * @return Result of the command execution as a String
     */
    private String executeCommand(CommandAction action) {
        try {
            Object result = action.execute();
            return String.valueOf(result);
        } catch (RemoteException e) {
            connected = false;
            return "Remote Exception: " + e.getMessage();
        }
    }

    private boolean checkDelete(String[] data) {
        return data.length == 2;
    }

    private boolean checkGet(String[] data) {
        return data.length == 2;
    }

    private boolean checkPut(String[] data) {
        return data.length == 3;
    }

    @FunctionalInterface
    interface CommandAction {
        Object execute() throws RemoteException;
    }
}
