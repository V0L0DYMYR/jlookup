package com.jlookup.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.jlookup.common.Utils.log;

public abstract class AbstractTask implements Runnable {

  protected final ExecutorService threadPool;
  protected final List<Future<Long>> startTimes = new ArrayList<>();
  protected final long startTime;

  protected AbstractTask(ExecutorService threadPool, long startTime) {

    this.threadPool = threadPool;
    this.startTime = startTime;
  }

  @Override
  public void run() {
    try {
      service();
    } finally {
      if (threadPool != null) {
        threadPool.shutdown();
      }
    }
  }

  protected void execute(Callable<Long> task) {
    startTimes.add(threadPool.submit(task));
  }

  protected abstract void service();

  public abstract String getThreadSuffix();

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
    }

    return 0L;
  }

  public abstract void stop();
}
