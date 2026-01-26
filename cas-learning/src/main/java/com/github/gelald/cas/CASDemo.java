package com.github.gelald.cas;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CASDemo {
    private static final AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 1000; i++) {
            executor.submit(counter::incrementAndGet);
        }

        executor.shutdown();
        boolean b = executor.awaitTermination(1, TimeUnit.MINUTES);
        System.out.println("最终计数: " + counter.get()); // 必定 = 1000
    }
}
