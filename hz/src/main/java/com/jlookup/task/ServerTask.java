package com.jlookup.task;

import com.jlookup.common.Utils;
import com.jlookup.handler.TimeHandler;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.jlookup.common.Utils.log;

public class ServerTask extends AbstractTask {

  private final int serverPort;
  private ServerSocket serverSocket;
  private boolean isStopped = false;


  private ServerTask(int port, long startTime, ExecutorService threadPool) {
    super(threadPool, startTime);
    this.serverPort = port;
  }

  public long getFistStart() {
    Long result = startTime;

    for (Future<Long> startFuture : startTimes) {
      Long nextStart = getStartTime(startFuture);
      log(String.format("received start_time {%d}", nextStart));
      result = Math.min(result, nextStart);
    }
    return result;
  }

  private Long getStartTime(Future<Long> startFuture) {
    try {
      return startFuture.get(2, TimeUnit.SECONDS);
    } catch (InterruptedException | TimeoutException | ExecutionException e) {
      log(e.getMessage());
      e.printStackTrace();
    }

    return 0L;
  }

  protected void service() {

    openServerSocket();

    while (!isStopped()) {

      int count = 1;
      try {
        Socket socket = serverSocket.accept();
        log("accepted " + socket.getRemoteSocketAddress());

        String serverName = String.format("%s[%d]", Utils.getThreadName(), count++);
        execute(new TimeHandler(socket, startTime, serverName));

      } catch (IOException e) {
        if (isStopped()) {
          log("Server Stopped.");
          break;
        }
        String message = "Error accepting client connection";
        log(message);
        throw new RuntimeException(message, e);
      }
    }
  }

  @Override
  public String getThreadSuffix() {
    return "server";
  }

  private synchronized boolean isStopped() {
    return this.isStopped;
  }

  public synchronized void stop() {
    this.isStopped = true;
    closeAndThrow(serverSocket);
  }

  private void closeAndThrow(Closeable closable) {
    try {
      closable.close();
    } catch (IOException e) {
      String message = "Error on close.";
      log(message);
      throw new RuntimeException(message, e);
    }
  }

  private void openServerSocket() {
    try {
      this.serverSocket = new ServerSocket(this.serverPort);
    } catch (IOException e) {
      String message = String.format("Cannot open port %d.", serverPort);
      log(message);
      throw new RuntimeException(message, e);
    }
  }

  public static class Builder {

    private long startTime;
    private int port;
    private ExecutorService threadPool;

    private Builder() {
    }

    public static Builder newBuilder() {
      return new Builder();
    }

    public Builder setStartTime(long startTime) {
      this.startTime = startTime;
      return this;
    }

    public Builder setPort(int port) {
      this.port = port;
      return this;
    }

    public Builder setThreadPool(ExecutorService pool) {
      checkNotNull(pool);
      this.threadPool = pool;
      return this;
    }

    public ServerTask build() {
      checkNotNull(threadPool);
      return new ServerTask(port, startTime, threadPool);
    }
  }
}

