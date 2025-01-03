package server.paxos;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Proposer role in Paxos.
 */
public class Proposer {
  private final ProposalNumber proposalNumber;
  private final Object proposedValue;
  private final List<Acceptor> acceptors;
  private final String proposerId;
  private final int quorumSize;
  private final Learner learner;

  public Proposer(String proposerId, Object proposedValue, List<Acceptor> acceptors, Learner learner) {
    this.proposerId = proposerId;
    this.proposedValue = proposedValue;
    this.acceptors = acceptors;
    this.proposalNumber = generateProposalNumber();
    this.quorumSize = (acceptors.size() / 2) + 1;
    this.learner = learner;
  }

  private ProposalNumber generateProposalNumber() {
    // Ensure unique proposal number by combining a random number with proposerId
    int randomNumber = new Random().nextInt(Integer.MAX_VALUE);
    return new ProposalNumber(randomNumber, proposerId);
  }

  public void propose() {
    try {
      // Phase 1: Prepare
      List<PaxosMessage> promises = new ArrayList<>();
      for (Acceptor acceptor : acceptors) {
        PaxosMessage prepareMsg = new PaxosMessage(PaxosMessage.Type.PREPARE, proposalNumber, null, null, null, proposerId);
        PaxosMessage response = acceptor.receivePrepare(prepareMsg);
        if (response != null && response.getType() == PaxosMessage.Type.PROMISE) {
          promises.add(response);
        }
      }

      if (promises.size() < quorumSize) {
        // Not enough promises
        System.out.println("Proposer " + proposerId + ": Not enough promises. Aborting proposal.");
        return;
      }

      // Determine the highest accepted proposal if any
      ProposalNumber highestAccepted = null;
      Object value = proposedValue;
      for (PaxosMessage promise : promises) {
        if (promise.getPrevAcceptedNumber() != null) {
          if (highestAccepted == null || promise.getPrevAcceptedNumber().compareTo(highestAccepted) > 0) {
            highestAccepted = promise.getPrevAcceptedNumber();
            value = promise.getPrevAcceptedValue();
          }
        }
      }

      // Phase 2: Accept Request
      List<PaxosMessage> acceptedResponses = new ArrayList<>();
      for (Acceptor acceptor : acceptors) {
        PaxosMessage acceptReq = new PaxosMessage(PaxosMessage.Type.ACCEPT_REQUEST, proposalNumber, null, null, value, proposerId);
        PaxosMessage response = acceptor.receiveAcceptRequest(acceptReq);
        if (response != null && response.getType() == PaxosMessage.Type.ACCEPTED) {
          acceptedResponses.add(response);
        }
      }

      if (acceptedResponses.size() >= quorumSize) {
        // Consensus achieved
        System.out.println("Proposer " + proposerId + ": Consensus achieved on value: " + value);
        learner.learn(value);
      } else {
        // Not enough acceptances
        System.out.println("Proposer " + proposerId + ": Not enough acceptances. Aborting proposal.");
      }

    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }
}
