package com.jlookup.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static com.jlookup.common.Utils.log;
import static com.jlookup.common.Utils.formatTime;

public class TimeHandler extends AbstractRequest {
  protected final Long startTime;

  public TimeHandler(Socket clientSocket, Long startTime, String name) {
    super(clientSocket, name);
    this.startTime = startTime;
  }

  public Long service(InputStream in, OutputStream out) throws IOException {
    String message = START_TIME_MSG + ":" + startTime;
    out.write(message.getBytes());
    out.flush();

    log(String.format("sent {%s} {%s}", message, formatTime(startTime)));
    String response = read(in);

    Long result = getTime(response);
    log(String.format("received {%s} {%s}", response, formatTime(result)));

    return result;
  }
}
