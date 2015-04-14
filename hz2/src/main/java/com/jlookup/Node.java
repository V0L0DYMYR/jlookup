package com.jlookup;

import com.hazelcast.config.Config;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;

import java.util.Set;

public class Node {
  HazelcastInstance hazelcastInstance;

  public Node() {
    Config config = new Config();
    hazelcastInstance = Hazelcast.newHazelcastInstance(config);
  }

  public void start() {
    Set<HazelcastInstance> instances = Hazelcast.getAllHazelcastInstances();

    if (instances.size() == 1) {
      Cluster cluster = hazelcastInstance.getCluster();
      Set<Member> members = cluster.getMembers();

      if (members.size() == 1) {
        System.out.println(hazelcastInstance.getName() + " STARTED!");
      }
    }
  }
}
