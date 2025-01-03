package server.command;

import server.MyKeyValueDB;

/**
 * Delete command that removes the key.
 */
public class DeleteCommand implements Command {
  private final String key;

  public DeleteCommand(String key) {
    this.key = key;
  }

  @Override
  public Object execute(MyKeyValueDB db) {
    return db.delete(key);
  }
}
