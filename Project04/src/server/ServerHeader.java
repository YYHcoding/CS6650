// File: src/server/ServerHeader.java
package server;

import java.io.Serializable;

/**
 * ServerHeader holds the host and port information of a server.
 */
public class ServerHeader implements Serializable {
  private static final long serialVersionUID = 1L;
  private final String host;
  private final int port;

  public ServerHeader(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  // Override equals and hashCode for proper functioning in HashSet
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof ServerHeader)) return false;
    ServerHeader other = (ServerHeader) obj;
    return this.port == other.port && this.host.equals(other.host);
  }

  @Override
  public int hashCode() {
    return host.hashCode() * 31 + port;
  }

  @Override
  public String toString() {
    return host + ":" + port;
  }
}
