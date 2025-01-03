package server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import util.KeyValueDB;

/**
 * The database of the server.
 */
public class MyKeyValueDB implements Serializable {

    private final Map<String, String> map;

    private int wait = 10000;

    //Using a single lock to perform hashmap operations(get/put/delete) to achieve mutual exclusion
    private static final Object LOCK = new Object();

    /**
     * Constructor for the database
     */
    public MyKeyValueDB() {
        super();
        map = new HashMap<>();
    }

    /**
     * Returns a copy of the database
     * @return A copy of the database
     */
    public MyKeyValueDB copy() {

        synchronized (LOCK) {
            MyKeyValueDB copyDB = new MyKeyValueDB();
            for(Map.Entry<String, String> entry: map.entrySet()) {
                copyDB.put(entry.getKey(), entry.getValue());
            }
            return copyDB;
        }
    }

    /**
     * Thread safe get operation
     */
    public String get(String key) {

        synchronized (LOCK) {
            if (!map.containsKey(key)) {
                return "";
            }
            return map.get(key);
        }

    }

    /**
     * Thread safe put operation
     */
    public boolean put(String key, String value) {


        synchronized (LOCK) {
            if (key.isBlank() || value.isBlank()) {
                return false;
            }
            map.put(key, value);
            return true;
        }

    }

    /**
     * Thread safe delete operation
     */
    public boolean delete(String key) {

        synchronized (LOCK) {
            if (!map.containsKey(key)) {
                return false;
            }
            map.remove(key);
            return true;
        }

    }

    /**
     * Adding random values to the db
     */
    public void populate() {
        put("NAME", "Yanhui Yang");
        put("ID", "002249965");
        put("GENDER", "Male");
        put("CLASS", "CS6650");
        put("GRADE", "A");
        put("Expensive", "Cheap");
        put("Friend", "Enemy");
        put("Happy", "Sad");
        put("Inferior", "Superior");
        put("Possible", "Impossible");
        put("Serious", "Trivial");
        put("Strong", "Weak");
    }

    /**
     * Returns the contents of database
     *
     * @return the contents of database in String format
     */
    public String getString() {
        StringBuilder sb = new StringBuilder();
        for (String key : map.keySet()) {
            sb.append("(").append(key).append(", ").append(map.get(key)).append(")\n");
        }
        return sb.toString();
    }
}
