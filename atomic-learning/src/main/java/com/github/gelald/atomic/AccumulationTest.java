package com.github.gelald.atomic;

import org.springframework.util.StopWatch;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

public class AccumulationTest {
    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        AtomicInteger atomicInteger = new AtomicInteger();
        stopWatch.start("AtomicInteger");
        for (int i = 0; i < 10000; i++) {
            atomicInteger.incrementAndGet();
        }
        stopWatch.stop();
        System.out.println("AtomicInteger: " + stopWatch.lastTaskInfo().getTimeMillis());

        LongAdder longAdder = new LongAdder();
        stopWatch.start("LongAdder");
        for (int i = 0; i < 10000; i++) {
            longAdder.increment();
        }
        stopWatch.stop();
        System.out.println("LongAdder: " + stopWatch.lastTaskInfo().getTimeMillis());

        LongAccumulator longAccumulator = new LongAccumulator(Long::sum, 1);
        longAccumulator.accumulate(2);
        System.out.println("longAccumulator " + longAccumulator.longValue());
        longAccumulator.accumulate(5);
        System.out.println("longAccumulator " + longAccumulator.longValue());
    }
}
