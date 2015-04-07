package com.jlookup.server;

import com.jlookup.task.AbstractTask;

import java.util.ArrayList;
import java.util.List;

import static com.jlookup.common.Utils.formatTime;
import static com.jlookup.common.Utils.log;

public class Node implements Runnable {

  private final List<AbstractTask> tasks;
  private String name;
  private long startTime;

  private Node(List<AbstractTask> tasks, String name, long startTime) {
    this.tasks = tasks;
    this.name = name;
    this.startTime = startTime;
  }

  @Override
  public void run() {
    log(String.format("started. {%s}", formatTime(startTime)));

    for (AbstractTask task : tasks) {
      Thread client = new Thread(task, String.format("[%s][%s]", name, task.getThreadSuffix()));
      client.setDaemon(true);
      client.start();
    }

    sleep(1000 * 15);


    Long minTime = 0L;
    for (AbstractTask task : tasks) {
      minTime = task.getFistStart();
      minTime = Math.min(minTime, task.getFistStart());
    }


    if (minTime == startTime) {
      String message = String.format("I'm first! {%s}", formatTime(minTime));
      log(message);
    }
  }

  private void sleep(long time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void stop() {
    for (AbstractTask task : tasks) {
      task.stop();
    }
  }

  public void start() {
    Thread thread = new Thread(this, String.format("[%s]", name));
    thread.setDaemon(true);
    thread.start();
  }

  public static class Builder {

    private List<AbstractTask> tasks;
    private String name;
    private long startTime;

    private Builder() {
    }

    public static Builder newBuilder() {
      return new Builder();
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setStartTime(long startTime) {
      this.startTime = startTime;
      return this;
    }

    public Node build() {
      return new Node(tasks, name, startTime);
    }

    public Builder addTask(AbstractTask task) {
      if (tasks == null) {
        tasks = new ArrayList<>();
      }
      tasks.add(task);
      return this;
    }
  }
}
