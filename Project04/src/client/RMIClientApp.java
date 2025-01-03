// File: src/client/RMIClientApp.java
package client;

import util.Log;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Main application class for RMI Client.
 * Supports interactive and batch modes.
 */
public class RMIClientApp {
    /**
     * Main method to start the RMI Client.
     *
     * @param args Command line arguments:
     *             <coordinator-host> <coordinator-port> [--batch]
     */
    public static void main(String[] args) {
        if (args.length < 2 || args.length > 3) {
            System.out.println("Usage: java client.RMIClientApp <coordinator-host> <coordinator-port> [--batch]");
            System.exit(1);
        }

        String coordinatorHost = args[0];
        int coordinatorPort;

        try {
            coordinatorPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid coordinator port: " + args[1]);
            System.exit(1);
            return; // Unreachable, but required for compilation
        }

        boolean batchMode = false;
        if (args.length == 3) {
            if (args[2].equalsIgnoreCase("--batch")) {
                batchMode = true;
            } else {
                System.out.println("Unknown option: " + args[2]);
                System.out.println("Usage: java client.RMIClientApp <coordinator-host> <coordinator-port> [--batch]");
                System.exit(1);
            }
        }

        try {
            RMIClient client = new RMIClient(coordinatorHost, coordinatorPort, batchMode);
            client.execute();
        } catch (NotBoundException e) {
            Log.logln("Coordinator Server not bound: " + e.getMessage());
            System.out.println("Error: Coordinator Server not bound.");
        } catch (RemoteException e) {
            Log.logln("Remote Exception: " + e.getMessage());
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            Log.logln("Unexpected Exception: " + e.getMessage());
            e.printStackTrace();
            System.out.println("An unexpected error occurred.");
        }
    }
}
