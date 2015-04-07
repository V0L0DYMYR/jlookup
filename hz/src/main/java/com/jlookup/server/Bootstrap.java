package com.jlookup.server;

import com.jlookup.common.Host;
import com.jlookup.task.ClientTask;
import com.jlookup.task.ServerTask;

import java.util.concurrent.Executors;

import static com.jlookup.common.Utils.log;

public class Bootstrap {


  public static void main(String[] args) throws InterruptedException {
    Thread.currentThread().setName("[main]");
    Host hostOne = Host.at("localhost:8001");
    Host hostTwo = Host.at("localhost:8002");
    Host hostThree = Host.at("localhost:8003");

    //Node node = createNode("FIRST", 8001, hostTwo, hostThree);
    //Node node = createNode("SECOND", 8002, hostOne, hostThree);
    Node node = createNode("THIRD", 8003, hostOne, hostTwo);
    node.start();


    Thread.sleep(200 * 1000);
    node.stop();
    log("finish.");
  }

  private static Node createNode(String name, int port, Host... hosts) {

    long startTime = System.currentTimeMillis();

    ClientTask clientTask = ClientTask.Builder.newBuilder()
        .setStartTime(startTime)
        .setThreadPool(Executors.newFixedThreadPool(5))
        .addAllRemoteHosts(hosts)
        .build();

    ServerTask serverTask =
        ServerTask.Builder.newBuilder()
            .setPort(port)
            .setStartTime(startTime)
            .setThreadPool(Executors.newFixedThreadPool(5))
            .build();

    return Node.Builder.newBuilder()
        .addTask(serverTask)
        .addTask(clientTask)
        .setName(name)
        .setStartTime(startTime)
        .build();
  }

}
