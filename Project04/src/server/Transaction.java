package server;

import server.command.Command;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A transaction contains a list of commands that should be performed in an all-or-none manner.
 */
public class Transaction implements Serializable {
  private final ServerHeader callerHeader;
  private final String id;
  private final List<Command> commandList;
  private final List<Object> result;

  /**
   * Constructor for transaction
   *
   * @param id           the id of the transaction
   * @param callerHeader server header of the transaction manager
   */
  public Transaction(String id, ServerHeader callerHeader) {
    this.id = id;
    this.callerHeader = callerHeader;
    this.commandList = new ArrayList<>();
    this.result = new ArrayList<>();
  }

  /**
   * Adds a command to the transaction
   *
   * @param command the command to add
   */
  public void addCommand(Command command) {
    commandList.add(command);
  }

  /**
   * Returns the id of the transaction
   *
   * @return the id of the transaction
   */
  public String getId() {
    return id;
  }

  /**
   * Returns the results of the transaction commands
   *
   * @return the results
   */
  public List<Object> getResult() {
    return result;
  }

  /**
   * Executes the transaction
   *
   * @param db the db on which to perform transaction
   * @return the results of the transaction commands
   */
  public List<Object> execute(MyKeyValueDB db) {
    for (Command command : commandList) {
      result.add(command.execute(db));
    }
    return result;
  }

  /**
   * Returns the header of the transaction manager
   *
   * @return the header of the transaction manager
   */
  public ServerHeader getCallerHeader() {
    return callerHeader;
  }
}
