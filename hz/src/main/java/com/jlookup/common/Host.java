package com.jlookup.common;

public class Host {

  private final String name;
  private final int port;

  private Host(String name, int port) {
    this.name = name;
    this.port = port;
  }

  public static Host at(String name, int port) {
    return new Host(name, port);
  }

  public static Host at(String fullAddress) {
    String[] parts = fullAddress.split(":");
    int port = Integer.parseInt(parts[1]);
    return new Host(parts[0], port);
  }

  public String getName() {
    return name;
  }

  public int getPort() {
    return port;
  }
}
