package com.github.gelald.cas;

import org.springframework.util.StopWatch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

public class AccumulationTest {
    public static void main(String[] args) throws InterruptedException {
        int threads = 100;
        int iterations = 10000;

        StopWatch stopWatch = new StopWatch();
        // 测试 AtomicInteger 自增性能
        // 使用 CAS 保证原子性，但在高并发下存在大量失败重试
        AtomicInteger atomicInteger = new AtomicInteger(0);
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        stopWatch.start("AtomicInteger");
        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < iterations; j++) {
                    atomicInteger.incrementAndGet();
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        stopWatch.stop();
        System.out.println("AtomicInteger: " + stopWatch.lastTaskInfo().getTimeMillis());

        // 测试 LongAdder 自增性能
        // 将累加操作分散到多个 Cell 中，最后再合并结果
        // 在高并发场景下性能优于 AtomicInteger
        LongAdder longAdder = new LongAdder();
        executor = Executors.newFixedThreadPool(threads);

        stopWatch.start("LongAdder");
        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < iterations; j++) {
                    longAdder.increment();
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        stopWatch.stop();
        System.out.println("LongAdder: " + stopWatch.lastTaskInfo().getTimeMillis());

        // 测试 LongAccumulator 累加功能
        // identity 为初始值 1，累加函数为 Long::sum
        LongAccumulator longAccumulator = new LongAccumulator(Long::sum, 1);
        // 累加 2，结果为 1 + 2 = 3
        longAccumulator.accumulate(2);
        System.out.println("longAccumulator " + longAccumulator.longValue());
        // 继续累加 5，结果为 3 + 5 = 8
        longAccumulator.accumulate(5);
        System.out.println("longAccumulator " + longAccumulator.longValue());
    }
}
