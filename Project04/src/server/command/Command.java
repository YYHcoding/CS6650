package server.command;

import java.io.Serializable;

import server.MyKeyValueDB;

/**
 * Command interface for executing an operation on the server.
 */
public interface Command extends Serializable {

  /**
   * The function specific to each command that it has to execute.
   * @param db The database(model) on which to perform execution
   * @return The return value specific to the command.
   */
  Object execute(MyKeyValueDB db);
}
