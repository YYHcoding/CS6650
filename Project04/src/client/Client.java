
package client;

import java.rmi.RemoteException;

/**
 * Client interface defines the methods that the client must implement.
 */
public interface Client {
  void execute() throws RemoteException;
}
