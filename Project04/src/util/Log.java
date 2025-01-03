package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A class to log the requests and responses of client or server to a file
 */
public class Log {

  /**
   * Constructor for Log. (Does nothing)
   */
  public Log() {
  }

  /**
   * Logs without \n. Similar to System.out.print(s)
   *
   * @param s String to log
   */
  public static void log(String s) {
    s = DataUtils.getCurrentTime() + ": " + s;
    System.out.print(s);
  }

  /**
   * Logs with \n. Similar to System.out.println(s)
   *
   * @param s String to log
   */
  public static void logln(String s) {
    s = DataUtils.getCurrentTime() + ": " + s;
    System.out.println(s);
  }
}
