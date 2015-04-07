package com.jlookup.task;

import com.jlookup.common.Host;
import com.jlookup.common.Utils;
import com.jlookup.handler.TimeHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.jlookup.common.Utils.log;

public class ClientTask extends AbstractTask {

  private List<Host> remoteNodes;

  private ClientTask(List<Host> remoteNodes, long startTime, ExecutorService threadPool) {
    super(threadPool, startTime);
    this.remoteNodes = remoteNodes;
  }

  protected void service() {

    int count = 1;

    for (Host host : remoteNodes) {
      try {
        Socket client = new Socket(host.getName(), host.getPort());
        log(String.format("connected to %s", host));
        String newThreadName = String.format("%s[%d]", Utils.getThreadName(), count++);

        execute(new TimeHandler(client, startTime, newThreadName));
      } catch (IOException e) {
        log(e.getMessage());
      }
    }
  }

  @Override
  public String getThreadSuffix() {
    return "client";
  }

  @Override
  public void stop() {

  }

  public static class Builder {

    private List<Host> remoteHosts = new ArrayList<>();
    private long startTime;
    private ExecutorService threadPool;

    private Builder() {
    }

    public static Builder newBuilder() {
      return new Builder();
    }

    public Builder addRemoteHost(Host address) {
      checkNotNull(address);
      remoteHosts.add(address);
      return this;
    }

    public Builder setStartTime(long startTime) {
      this.startTime = startTime;
      return this;
    }

    public Builder setThreadPool(ExecutorService pool) {
      checkNotNull(pool);
      this.threadPool = pool;
      return this;
    }

    public ClientTask build() {
      checkNotNull(startTime);
      checkNotNull(threadPool);
      return new ClientTask(remoteHosts, startTime, threadPool);
    }

    public Builder addAllRemoteHosts(Host[] hosts) {
      for (Host host : hosts) {
        remoteHosts.add(host);
      }
      return this;
    }
  }
}
