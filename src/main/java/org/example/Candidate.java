package org.example;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.example.ZookeeperConnection.ELECTION_NAMESPACE;

public class Candidate extends Thread implements Watcher {

    private static final AtomicInteger counter = new AtomicInteger();
    private final int candidateNumber;
    private final long shutdownAfter;
    private final ZooKeeper zooKeeper;
    private String currentZNodeName;

    public Candidate() {
        super();
        candidateNumber = counter.incrementAndGet();
        Random rand = new Random();
        shutdownAfter = rand.nextLong(10_000);
        this.zooKeeper = ZookeeperConnection.connectToZookeeper();
    }

    public void close() {
        try {
            zooKeeper.close();
        } catch (InterruptedException e) {
            // ignore
        }
    }

    @Override
    public void run() {
        volunteerForLeadership();
        System.out.println("Candidate with number " + candidateNumber
                + " registered for leader election with name " + currentZNodeName);

        electLeader();

        try {
            Thread.sleep(shutdownAfter);
        } catch (InterruptedException e) {
            // ignore
        }
        System.out.println("Shutdown Candidate with number " + candidateNumber
                + " registered for leader election with name " + currentZNodeName);
        close();
    }

    public void volunteerForLeadership() {
        try {
            String zNodePrefix = ELECTION_NAMESPACE + "/c_";
            String zNodeFullPath =
                    zooKeeper.create(zNodePrefix, new byte[] {}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            this.currentZNodeName = zNodeFullPath.replace(ELECTION_NAMESPACE + "/", "");

        } catch (KeeperException | InterruptedException e) {
            // ignore
        }
    }

    public void electLeader() {
        try {
            List<String> children = zooKeeper.getChildren(ELECTION_NAMESPACE, false);
            Collections.sort(children);

            String smallestChild = children.get(0);

            if (smallestChild.equals(currentZNodeName)) {
                System.out.println("\nI'm Candidate with number " + candidateNumber
                        + " registered for leader election with name " + currentZNodeName
                        + ". I'm a leader now!!!!\n");
            }

            watchTargetZNode();
        } catch (KeeperException | InterruptedException e) {
            // ignore
        }
    }

    public void watchTargetZNode() throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists(ELECTION_NAMESPACE, this);
        if (stat == null) {
            return;
        }

        zooKeeper.getChildren(ELECTION_NAMESPACE, this);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()) {
            case NodeChildrenChanged -> electLeader();
            default -> {}
        }
    }
}
