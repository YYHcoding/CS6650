package server.command;

import server.MyKeyValueDB;

/**
 * Get command that retrieves the value of a key.
 */
public class GetCommand implements Command {
  private final String key;

  public GetCommand(String key) {
    this.key = key;
  }

  @Override
  public Object execute(MyKeyValueDB db) {
    return db.get(key);
  }
}
