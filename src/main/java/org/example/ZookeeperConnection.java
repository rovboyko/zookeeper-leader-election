package org.example;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class ZookeeperConnection {

    public static final String ZOOKEEPER_ADDRESS = "localhost:2181,localhost:2182,localhost:2183";
    private static final int SESSION_TIMEOUT = 3000;
    public static final String ELECTION_NAMESPACE = "/election";

    public static ZooKeeper connectToZookeeper() {
        try {
            return new ZooKeeper(ZOOKEEPER_ADDRESS, SESSION_TIMEOUT, null);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
