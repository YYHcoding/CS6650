package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * A Utility class for some common methods between different classes
 */
public class DataUtils {

  /**
   * Validates the Client Command Line Arguments
   *
   * @param args Command Line Arguments
   */
  public static void validateClientArguments(String[] args) {
    if (args.length != 2) {
      System.out.println("Improper number of arguments! Required 2 arguments: host port");
      System.exit(1);
    }

    try {
      Integer.parseInt(args[1]);
    } catch (NumberFormatException e) {
      System.out.println(args[1] + " is not a valid port number!!");
      System.exit(1);
    }
  }

  /**
   * Validates the Server Command Line Arguments
   *
   * @param args Command Line Arguments
   */
  public static void validateServerArguments(String[] args) {
    if (args.length != 3) {
      System.out.println("Improper number of arguments! Required 2 arguments: host port");
      System.exit(1);
    }

    try {
      Integer.parseInt(args[1]);
    } catch (NumberFormatException e) {
      System.out.println(args[1] + " is not a valid port number!!");
      System.exit(1);
    }
  }

  /**
   * Simple encoder to be used by UDP Datagram packets
   *
   * @param input The string to be encoded
   * @return The encoded String
   */
  public static String encode(String input) {
    byte[] bytes = input.getBytes();
    return Base64.getEncoder().encodeToString(bytes);
  }

  /**
   * Simple decoder to be used by UDP Datagram Packets
   *
   * @param input The string to be decoded
   * @return The decoded String
   */
  public static String decode(String input) {
    byte[] bytes = Base64.getDecoder().decode(input.getBytes());
    return new String(bytes);
  }

  /**
   * Gives currenttime in yyyy/MM/dd HH:mm:ss:SSS format
   *
   * @return The current time with millisecond precision
   */
  public static String getCurrentTime() {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss:SSS");
    LocalDateTime now = LocalDateTime.now();
    return dtf.format(now);
  }
}
