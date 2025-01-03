package server.paxos;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a proposal number in Paxos.
 */
public class ProposalNumber implements Comparable<ProposalNumber>, Serializable {
  private final int number;
  private final String proposerId;

  public ProposalNumber(int number, String proposerId) {
    this.number = number;
    this.proposerId = proposerId;
  }

  public int getNumber() {
    return number;
  }

  public String getProposerId() {
    return proposerId;
  }

  @Override
  public int compareTo(ProposalNumber o) {
    int cmp = Integer.compare(this.number, o.number);
    if (cmp == 0) {
      return this.proposerId.compareTo(o.proposerId);
    }
    return cmp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ProposalNumber that = (ProposalNumber) o;
    return number == that.number && proposerId.equals(that.proposerId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(number, proposerId);
  }

  @Override
  public String toString() {
    return "(" + number + ", " + proposerId + ")";
  }
}
