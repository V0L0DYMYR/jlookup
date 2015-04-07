package com.jlookup.handler;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

import static com.jlookup.common.Utils.log;

public abstract class AbstractRequest implements Callable<Long> {

  protected final Socket socket;
  protected final String START_TIME_MSG = "START_TIME";
  private String name;

  public AbstractRequest(Socket clientSocket, String name) {
    this.socket = clientSocket;
    this.name = name;
  }

  public abstract Long service(InputStream in, OutputStream out) throws IOException;

  @Override
  public Long call() throws Exception {
    Thread.currentThread().setName(name);
    Long result = Long.MAX_VALUE;
    OutputStream out = null;
    InputStream in = null;

    try {
      out = socket.getOutputStream();
      in = socket.getInputStream();

      result = service(in, out);

      out.flush();

    } catch (Throwable t) {
      log(t.getMessage());
      t.printStackTrace();
    } finally {
      close(out, in, socket);
    }
    return result;
  }

  private void close(Closeable... closeables) throws IOException {
    for (Closeable closeable : closeables) {
      if (closeable != null) {
        closeable.close();
      }
    }
  }

  protected Long getTime(String request) {
    String[] requestPieces = request.split(":");
    Long result = 0L;
    String requestHeader = requestPieces[0];

    if (START_TIME_MSG.equals(requestHeader)) {
      result = Long.parseLong(requestPieces[1]);
    }
    return result;
  }

  protected String read(InputStream input) throws IOException {
    StringBuilder request = new StringBuilder();
    byte[] buffer = new byte[1024];
    int bufLength;

    while (input.available() > 0) {
      bufLength = input.read(buffer);
      request.append(new String(buffer, 0, bufLength));
    }

    return request.toString();
  }
}
