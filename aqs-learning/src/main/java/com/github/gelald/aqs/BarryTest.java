package com.github.gelald.aqs;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class BarryTest {
    static class CountDownLatchTest {
        public static void main(String[] args) throws InterruptedException {
            int workerCount = 3;
            CountDownLatch latch = new CountDownLatch(workerCount);

            System.out.println("===== CountDownLatch 测试 =====");
            System.out.println("主线程: 启动 " + workerCount + " 个 worker");

            for (int i = 1; i <= workerCount; i++) {
                new Thread(() -> {
                    try {
                        System.out.println(Thread.currentThread().getName() + ": 开始工作");
                        Thread.sleep(1000);
                        System.out.println(Thread.currentThread().getName() + ": 工作完成，调用 countDown()");
                        latch.countDown(); // 通知完成
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }, "Worker-" + i).start();
            }

            System.out.println("主线程调用 await() 等待所有 worker 完成");
            latch.await(); // 主线程等待
            System.out.println("主线程: 所有 worker 完成，继续执行");

            // 尝试重用（会失败！）
            System.out.println("\n⚠️ 尝试重用 CountDownLatch...");
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + ": 再次开始工作");
                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName() + ": 工作再次完成，再次调用 countDown()");
                    latch.countDown(); // 无效果
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Worker-1").start();

            System.out.println("主线程尝试再次调用 await() 来等待第二次 worker 完成");
            latch.await(); // 立即通过（因为 state 已归0）
            System.out.println("主线程: 再次 await() 直接通过！证明不可重用");
        }
    }

    static class CyclicBarrierTest {
        public static void main(String[] args) throws InterruptedException {
            int parties = 3;
            CyclicBarrier barrier = new CyclicBarrier(parties, () -> {
                System.out.println("→→→ 阶段完成回调: 所有线程到达屏障 ←←←");
            });

            System.out.println("\n===== CyclicBarrier 测试 =====");

            // 第一阶段
            System.out.println("第一阶段: " + parties + " 个线程互相等待");
            for (int i = 1; i <= parties; i++) {
                new Thread(() -> {
                    try {
                        System.out.println(Thread.currentThread().getName() + ": 到达第一阶段屏障, 调用 await()");
                        int await = barrier.await();// 等待其他线程
                        System.out.println(Thread.currentThread().getName() + ": 通过第一阶段屏障, 唤醒, index: " + await);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, "Thread-" + i).start();
            }
            Thread.sleep(2000); // 等待第一阶段完成

            // 第二阶段（重用）
            System.out.println("\n✅ 重用 CyclicBarrier 进行第二阶段");
            for (int i = 1; i <= parties; i++) {
                new Thread(() -> {
                    try {
                        System.out.println(Thread.currentThread().getName() + ": 到达第二阶段屏障, 调用 await()");
                        int await = barrier.await();
                        System.out.println(Thread.currentThread().getName() + ": 通过第二阶段屏障, 唤醒, index: " + await);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, "Thread-" + i).start();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CountDownLatchTest.main(args);
        Thread.sleep(1000);
        CyclicBarrierTest.main(args);
    }
}
