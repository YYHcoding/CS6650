package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * An instance of a Server.
 */
public interface Server extends Remote {

  /**
   * Starts a server, listening at port based on the protocol it is created.
   */
  void start() throws RemoteException;

  /**
   * Returns true if the server can commit this transaction
   * @param transaction The transaction to commit
   * @return true if the server can commit this transaction
   */
  boolean canCommit(Transaction transaction) throws RemoteException;

  /**
   * Asking the server to commit the transaction
   * @param transaction The transaction to commit
   */
  void doCommit(Transaction transaction) throws RemoteException;

  /**
   * Asking the server to abort the transaction
   * @param transaction The transaction to abort
   */
  void doAbort(Transaction transaction) throws RemoteException;

  /**
   * Telling the manager that the transaction is committed from server
   * @param transaction The committed transaction
   * @param header The server header that committed the transaction
   */
  void haveCommitted(Transaction transaction, ServerHeader header) throws RemoteException;

  /**
   * Asking the manager the decision of the transaction
   * @param transaction the transaction
   * @return Manager returns the decision of the transaction
   */
  boolean getDecision(Transaction transaction) throws RemoteException;

  /**
   * The starting method run by the manager to perform the two-phase commit protocol for distributed transaction
   * @param transaction The transaction to commit
   * @return The return values of the transaction
   */
  List<Object> performTransaction(Transaction transaction) throws RemoteException;

  /**
   * Gives a deep copy of the database object
   * @return the deep copy of the database object
   */
  MyKeyValueDB getDBCopy() throws RemoteException;

  /**
   * Gives the server header including the host and port of the server
   * @return the server header
   */
  ServerHeader getServerHeader() throws RemoteException;
}
