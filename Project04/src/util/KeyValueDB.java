package util;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for Key-Value Store
 */
public interface KeyValueDB extends Remote {
    /**
     * Gets the value of the given key
     *
     * @param key The key
     * @return The value stored for the key or "" if key is not present
     */
    String get(String key) throws RemoteException;

    /**
     * Puts the (key, value) pair in the database
     *
     * @param key   The key
     * @param value The value
     * @return true if operation is successful else false
     */
    boolean put(String key, String value) throws RemoteException;

    /**
     * Deletes the key-value pair in the database
     *
     * @param key The key
     * @return true if operation is successful, else false
     */
    boolean delete(String key) throws RemoteException;

    /**
     * Populates the database with some key-value pairs
     */
    void populate() throws RemoteException;

    /**
     * Returns the contents of database
     *
     * @return the contents of database in String format
     */
    String getString() throws RemoteException;
}
