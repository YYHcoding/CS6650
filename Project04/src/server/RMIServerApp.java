// File: src/server/RMIServerApp.java
package server;

import util.DataUtils;

import java.rmi.RemoteException;

/**
 * RMI Server Application
 */
public class RMIServerApp {

    /**
     * Main Method to start RMI Server
     *
     * @param args Required args: <server-name> <port> <coordinator-host>
     */
    public static void main(String[] args) {

        // Validate command line arguments
        if (args.length != 3) {
            System.err.println("Usage: java server.RMIServerApp <server-name> <port> <coordinator-host>");
            System.exit(1);
        }

        String serverName = args[0];
        int port;
        String coordinatorHost = args[2];

        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number: " + args[1]);
            System.exit(1);
            return; // Unreachable, but required for compilation
        }

        // Optionally validate server arguments
        DataUtils.validateServerArguments(args);

        // Create and start the RMI Server
        try {
            RMIServer server = new RMIServer(serverName, port, coordinatorHost);
            server.start();
            System.out.println("RMIServer '" + serverName + "' started at host: " + server.header.getHost() + ", port: " + server.header.getPort());
        } catch (RemoteException e) {
            System.err.println("Error starting RMIServer: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
