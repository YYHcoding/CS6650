package server.paxos;

import java.io.Serializable;

/**
 * Represents a message in the Paxos algorithm.
 */
public class PaxosMessage implements Serializable {
  public enum Type {
    PREPARE,
    PROMISE,
    ACCEPT_REQUEST,
    ACCEPTED,
    LEARN
  }

  private final Type type;
  private final ProposalNumber proposalNumber;
  private final ProposalNumber prevAcceptedNumber;
  private final Object prevAcceptedValue;
  private final Object value;
  private final String senderId;

  // Constructor for PROMISE and ACCEPTED messages
  public PaxosMessage(Type type, ProposalNumber proposalNumber, ProposalNumber prevAcceptedNumber,
                      Object prevAcceptedValue, Object value, String senderId) {
    this.type = type;
    this.proposalNumber = proposalNumber;
    this.prevAcceptedNumber = prevAcceptedNumber;
    this.prevAcceptedValue = prevAcceptedValue;
    this.value = value;
    this.senderId = senderId;
  }

  // Getters
  public Type getType() {
    return type;
  }

  public ProposalNumber getProposalNumber() {
    return proposalNumber;
  }

  public ProposalNumber getPrevAcceptedNumber() {
    return prevAcceptedNumber;
  }

  public Object getPrevAcceptedValue() {
    return prevAcceptedValue;
  }

  public Object getValue() {
    return value;
  }

  public String getSenderId() {
    return senderId;
  }
}
