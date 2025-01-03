// File: src/server/CoordinatorServer.java
package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * CoordinatorServer interface defines the methods that the Coordinator Server must implement.
 */
public interface CoordinatorServer extends Remote {
  int PORT = 9999; // Default port for Coordinator Server

  void addServer(ServerHeader server) throws RemoteException;

  List<ServerHeader> getServerList() throws RemoteException;

  String SERVER_LIST_SERVICE = "ServerListService"; // Service name for RMI lookup
}
