package server.paxos;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Acceptor extends Remote {
  PaxosMessage receivePrepare(PaxosMessage message) throws RemoteException;
  PaxosMessage receiveAcceptRequest(PaxosMessage message) throws RemoteException;
}
