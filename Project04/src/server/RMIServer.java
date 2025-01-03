// File: src/server/RMIServer.java
package server;

import server.command.DeleteCommand;
import server.command.PutCommand;
import server.command.Command;
import server.paxos.Acceptor;
import server.paxos.Learner;
import server.paxos.PaxosMessage;
import server.paxos.Proposer;
import server.paxos.ProposalNumber;
import util.KeyValueDB;
import util.Log;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Access: package-protected
 * Class for RMI Server
 */
public class RMIServer extends UnicastRemoteObject implements KeyValueDB, Server, Acceptor {
    private static final long serialVersionUID = 1L;

    private final int port;
    final ServerHeader header;
    private final MyKeyValueDB db;
    private CoordinatorServer coordinatorServer;

    // Paxos related
    private ProposalNumber promisedProposal;
    private ProposalNumber acceptedProposal;
    private Object acceptedValue;
    private boolean isActive;

    private final List<Acceptor> acceptors;
    private final Learner learner;

    protected RMIServer(String host, int port, String coordinatorHost) throws RemoteException {
        super(port);
        this.port = port;
        this.header = new ServerHeader(host, this.port);
        this.db = new MyKeyValueDB();
        this.db.populate();
        this.isActive = true;

        this.acceptors = new ArrayList<>();
        this.acceptors.add(this); // Server acts as its own acceptor

        this.learner = new Learner((acceptors.size() / 2) + 1);

        // Simulate random failures for the acceptor
        startRandomFailureSimulation();
    }

    public void start() throws RemoteException {
        try {
            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind("KeyValueDBService", this);

            // Locate the coordinator
            Registry coordinatorRegistry = LocateRegistry.getRegistry("my-rmi-coordinator", CoordinatorServer.PORT);
            coordinatorServer = (CoordinatorServer) coordinatorRegistry.lookup("ServerListService");

            // Register with the coordinator
            coordinatorServer.addServer(header);

            System.out.println("RMIServer started at host: " + header.getHost() + ", port: " + header.getPort());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Acceptor Methods
    @Override
    public synchronized PaxosMessage receivePrepare(PaxosMessage message) throws RemoteException {
        if (!isActive) throw new RemoteException("Acceptor is down");

        if (promisedProposal == null || message.getProposalNumber().compareTo(promisedProposal) > 0) {
            promisedProposal = message.getProposalNumber();
            return new PaxosMessage(PaxosMessage.Type.PROMISE, promisedProposal, acceptedProposal, acceptedValue, null, header.getHost());
        } else {
            // Ignore lower proposal numbers
            return null;
        }
    }

    @Override
    public synchronized PaxosMessage receiveAcceptRequest(PaxosMessage message) throws RemoteException {
        if (!isActive) throw new RemoteException("Acceptor is down");

        if (promisedProposal == null || message.getProposalNumber().compareTo(promisedProposal) >= 0) {
            promisedProposal = message.getProposalNumber();
            acceptedProposal = message.getProposalNumber();
            acceptedValue = message.getValue();
            return new PaxosMessage(PaxosMessage.Type.ACCEPTED, acceptedProposal, null, null, acceptedValue, header.getHost());
        } else {
            // Reject the proposal
            return null;
        }
    }

    // KeyValueDB Methods
    @Override
    public synchronized String get(String key) throws RemoteException {
        return db.get(key);
    }

    @Override
    public synchronized boolean put(String key, String value) throws RemoteException {
        // Initiate Paxos to agree on the PUT operation
        PutCommand putCommand = new PutCommand(key, value);
        return proposeOperation(putCommand);
    }

    @Override
    public synchronized boolean delete(String key) throws RemoteException {
        // Initiate Paxos to agree on the DELETE operation
        DeleteCommand deleteCommand = new DeleteCommand(key);
        return proposeOperation(deleteCommand);
    }

    @Override
    public synchronized void populate() throws RemoteException {
        db.populate();
    }

    @Override
    public synchronized String getString() throws RemoteException {
        return db.getString();
    }

    @Override
    public synchronized MyKeyValueDB getDBCopy() throws RemoteException {
        return db.copy();
    }

    @Override
    public synchronized ServerHeader getServerHeader() throws RemoteException {
        return header;
    }

    // Server Interface Methods
    @Override
    public boolean canCommit(Transaction transaction) throws RemoteException {
        // Implement your canCommit logic here
        // For simplicity, always return true
        return true;
    }

    @Override
    public void doCommit(Transaction transaction) throws RemoteException {
        // Implement your doCommit logic here
        // Execute all commands in the transaction
        transaction.execute(db);
        System.out.println("Transaction " + transaction.getId() + " committed.");
    }

    @Override
    public void doAbort(Transaction transaction) throws RemoteException {
        // Implement your doAbort logic here
        // For simplicity, do nothing
        System.out.println("Transaction " + transaction.getId() + " aborted.");
    }

    @Override
    public void haveCommitted(Transaction transaction, ServerHeader header) throws RemoteException {
        // Implement logic to handle a committed transaction from another server
        System.out.println("Received commit notification for Transaction " + transaction.getId() + " from " + header.getHost() + ":" + header.getPort());
    }

    @Override
    public boolean getDecision(Transaction transaction) throws RemoteException {
        // Implement logic to get the decision of a transaction
        // For simplicity, return true
        return true;
    }

    @Override
    public List<Object> performTransaction(Transaction transaction) throws RemoteException {
        // Implement the two-phase commit protocol here
        // For simplicity, directly commit the transaction
        doCommit(transaction);
        List<Object> results = transaction.getResult();
        return results;
    }

    // Paxos Helper Method
    private boolean proposeOperation(Command command) {
        Proposer proposer = new Proposer(header.getHost(), command, acceptors, learner);
        proposer.propose();

        // For simplicity, assume that if learner has learned the value, the operation is successful
        // In a real implementation, you'd have more robust checks
        // Here, we'll simulate a short wait and then check if the value is learned

        try {
            Thread.sleep(1000); // Wait for consensus
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // In a real scenario, you'd retrieve the learned value and verify
        // Here, we'll assume it's successful
        // Apply the command to the database
        command.execute(db);
        return true;
    }

    // Simulate random failures for the acceptor
    private void startRandomFailureSimulation() {
        new Thread(() -> {
            Random random = new Random();
            while (true) {
                try {
                    // Wait for a random time between 5 to 15 seconds
                    long sleepTime = 5000 + random.nextInt(10000);
                    Thread.sleep(sleepTime);
                    // Fail the acceptor
                    isActive = false;
                    System.out.println("Acceptor " + header.getHost() + ":" + header.getPort() + " has FAILED.");
                    // Stay failed for a random time between 2 to 5 seconds
                    sleepTime = 2000 + random.nextInt(3000);
                    Thread.sleep(sleepTime);
                    // Recover the acceptor
                    isActive = true;
                    System.out.println("Acceptor " + header.getHost() + ":" + header.getPort() + " has RECOVERED.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Note: No main method here. Use RMIServerApp.java to run this server.
}
