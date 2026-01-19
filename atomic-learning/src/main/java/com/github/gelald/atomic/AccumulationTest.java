package com.github.gelald.atomic;

import org.springframework.util.StopWatch;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

/**
 * 原子类累加性能测试
 * 
 * 对比 AtomicInteger、LongAdder 和 LongAccumulator 的性能差异
 * - AtomicInteger: 基于 CAS 操作，高并发下性能较差
 * - LongAdder: 分段累加，高并发下性能更好，适合统计场景
 * - LongAccumulator: 更通用的累加器，支持自定义累加函数
 */
public class AccumulationTest {
    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        
        // 测试 AtomicInteger 自增性能
        // 使用 CAS 保证原子性，但在高并发下存在大量失败重试
        AtomicInteger atomicInteger = new AtomicInteger();
        stopWatch.start("AtomicInteger");
        for (int i = 0; i < 10000; i++) {
            atomicInteger.incrementAndGet();
        }
        stopWatch.stop();
        System.out.println("AtomicInteger: " + stopWatch.lastTaskInfo().getTimeMillis());

        // 测试 LongAdder 自增性能
        // 将累加操作分散到多个 Cell 中，最后再合并结果
        // 在高并发场景下性能优于 AtomicInteger
        LongAdder longAdder = new LongAdder();
        stopWatch.start("LongAdder");
        for (int i = 0; i < 10000; i++) {
            longAdder.increment();
        }
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
