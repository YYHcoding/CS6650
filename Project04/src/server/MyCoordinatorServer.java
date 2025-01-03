// File: src/server/MyCoordinatorServer.java
package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import util.Log;

/**
 * Implementation of Coordinator Server. Its function is to store the details of all the servers in the application.
 * Should run before any other server starts.
 */
public class MyCoordinatorServer extends UnicastRemoteObject implements CoordinatorServer {
  private final int port;
  private final ServerHeader header;
  private final Set<ServerHeader> serverSet;

  protected MyCoordinatorServer(String host) throws RemoteException {
    super();
    this.port = CoordinatorServer.PORT;
    this.header = new ServerHeader(host, this.port);
    this.serverSet = new HashSet<>();
  }

  @Override
  public synchronized void addServer(ServerHeader server) throws RemoteException {
    serverSet.add(server);
    System.out.println("Coordinator: Added server " + server.getHost() + ":" + server.getPort());
  }

  @Override
  public synchronized List<ServerHeader> getServerList() throws RemoteException {
    // Remove servers that are not reachable
    Set<ServerHeader> removedSet = new HashSet<>();
    for (ServerHeader serverHeader : serverSet) {
      try {
        Registry registry = LocateRegistry.getRegistry(serverHeader.getHost(), serverHeader.getPort());
        Server server = (Server) registry.lookup("KeyValueDBService");
        server.getDBCopy(); // Simple call to check if server is reachable
      } catch (Exception e) {
        removedSet.add(serverHeader);
        System.out.println("Coordinator: Removed unreachable server " + serverHeader.getHost() + ":" + serverHeader.getPort());
      }
    }
    serverSet.removeAll(removedSet);
    System.out.println("Coordinator: Current server count: " + serverSet.size());
    return new ArrayList<>(serverSet);
  }

  public void start() {
    try {
      Registry registry = LocateRegistry.createRegistry(port);
      registry.rebind(SERVER_LIST_SERVICE, this);
      System.out.println("CoordinatorServer started at host: " + header.getHost() + ", port: " + header.getPort());
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    String coordinatorHost = args.length > 0 ? args[0] : "my-rmi-coordinator";
    int coordinatorPort = CoordinatorServer.PORT; // 9999

    try {
      MyCoordinatorServer coordinator = new MyCoordinatorServer(coordinatorHost);

      // Add Shutdown Hook for graceful shutdown
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        Log.logln("CoordinatorServer is shutting down...");
        // Example: Unbind from RMI registry, close resources, etc.
      }));

      coordinator.start();
      Log.logln("CoordinatorServer started at host: " + coordinatorHost + ", port: " + coordinatorPort);
    } catch (Exception e) {
      Log.logln("Error starting CoordinatorServer: " + e.getMessage());
      e.printStackTrace();
      System.exit(1); // Exit with error status
    }
  }
}
