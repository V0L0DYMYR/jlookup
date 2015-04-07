package com.jlookup.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

  private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss.SSS");

  private Utils() {
  }

  public static String getThreadName() {
    return Thread.currentThread().getName();
  }

  public static void log(String text) {
    System.out.println(getThreadName() + text);
  }

  public static String formatTime(Long time) {
    return formatter.format(new Date(time));
  }
}
