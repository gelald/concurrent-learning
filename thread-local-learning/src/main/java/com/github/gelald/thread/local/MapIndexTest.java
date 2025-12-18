package com.github.gelald.thread.local;

import java.util.concurrent.atomic.AtomicInteger;

public class MapIndexTest {
    // ThreadLocalMap 中定义的递增值，斐波那契乘数
    private static final int HASH_INCREMENT = 0x61c88647;
    public static void main(String[] args) {
        // ThreadLocalMap 的长度是 16，计算每一个数据的索引
        AtomicInteger nextHashCode = new AtomicInteger();
        for (int i = 0; i < 16; i++) {
            // 按照 hash & (length - 1) 的方式计算索引
            int index = nextHashCode.getAndAdd(HASH_INCREMENT) & 15;
            System.out.println("本轮ThreadLocalMap索引值: " + index);
        }

        for (int i = 0; i < 16; i++) {
            int index = (i ^ i >>> 16) & 15;
            System.out.println("本轮HashMap索引值: " + index);
        }
    }
}
