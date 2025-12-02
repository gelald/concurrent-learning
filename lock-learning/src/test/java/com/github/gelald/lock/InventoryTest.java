package com.github.gelald.lock;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class InventoryTest {
    private static final int THREAD_COUNT = 17;
    private static int stock;
    private static AtomicInteger atomicStock;
    private static ExecutorService executor;
    private static Lock lock;

    @BeforeAll
    static void initStock() {
        stock = 10;
        atomicStock = new AtomicInteger(10);
        lock = new ReentrantLock();
        executor = new ThreadPoolExecutor(
                20, 20, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(THREAD_COUNT),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @AfterAll
    static void shutdownExecutor() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    @Test
    void testCountDownInventory() {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // 创建多个子任务
        for (int i = 0; i < THREAD_COUNT; i++) {
            CompletableFuture.runAsync(() -> {
                try {
                    countDownLatch.await(); // 等待主线程信号
//                    boolean result = deductWithSynchronized(1);
//                    boolean result = deductWithLock(1);
                    boolean result = deductWithAtomic(1);
                    log.info("{} 任务执行完成, 状态: {}", Thread.currentThread().getName(), result);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, executor);
        }
        // 主线程准备
        try {
            Thread.sleep(2000); // 模拟主线程准备工作
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // 主线程发出开始信号
        log.info(" ========= 开始执行 ========= ");
        countDownLatch.countDown();
    }

    // 使用synchronized修饰方法，将该方法变为同步方法
    // 确保同一时间只有一个线程能进入该方法，从而保证对stock的操作是线程安全的
    public synchronized boolean deductWithSynchronized(int quantity) {
        // 判断当前库存是否足够扣减
        if (stock >= quantity) {
            // 库存足够，进行扣减操作
            stock -= quantity;
            return true;
        }
        return false;
    }

    public boolean deductWithLock(int quantity) {
        // 加锁，获取锁资源，确保同一时间只有一个线程能执行后续操作
        lock.lock();
        try {
            // 判断当前库存是否足够扣减
            if (stock >= quantity) {
                // 库存足够，进行扣减操作
                stock -= quantity;
                return true;
            }
            return false;
        } finally {
            // 无论try块中是否发生异常，都要在最后释放锁
            // 确保其他线程有机会获取锁并执行操作
            lock.unlock();
        }
    }

    public boolean deductWithAtomic(int quantity) {
        if (atomicStock.get() < quantity) {
            return false;
        }
        int retries = 0;
        final int MAX_RETRIES = 10;
        int oldValue;
        do {
            // 获取当前库存值
            oldValue = atomicStock.get();
            // 判断当前库存是否足够扣减
            if (oldValue < quantity) {
                return false;
            }
        } while (!atomicStock.compareAndSet(oldValue, oldValue - quantity) && retries++ < MAX_RETRIES);
        // compareAndSet方法会比较当前值是否等于预期值（oldValue）
        // 如果相等，则将当前值更新为新值（oldValue - quantity），并返回true；否则返回false
        return true;
    }
}
