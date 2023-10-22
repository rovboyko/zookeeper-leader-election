package org.example;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Template extends Thread {

    private static final AtomicInteger counter = new AtomicInteger();
    private final int candidateNumber;
    private final long shutdownAfter;

    public Template() {
        super();
        candidateNumber = counter.incrementAndGet();
        Random rand = new Random();
        shutdownAfter = rand.nextLong(10_000);
    }

    @Override
    public void run() {
        System.out.println("I'm Candidate_" + candidateNumber + ". I'm leader now.");
        try {
            Thread.sleep(shutdownAfter);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Shutdown Candidate_" + candidateNumber);
    }
}
