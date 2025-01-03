package server.paxos;

import java.util.HashMap;
import java.util.Map;

/**
 * Learner role in Paxos.
 */
public class Learner {
  private final Map<Object, Integer> learnedValues;
  private final int quorumSize;

  public Learner(int quorumSize) {
    this.learnedValues = new HashMap<>();
    this.quorumSize = quorumSize;
  }

  public synchronized void learn(Object value) {
    learnedValues.put(value, learnedValues.getOrDefault(value, 0) + 1);
    if (learnedValues.get(value) >= quorumSize) {
      System.out.println("Learner: Value " + value + " has been learned with quorum.");
      // Here you can implement actions upon learning the value, such as updating the database
    }
  }
}
