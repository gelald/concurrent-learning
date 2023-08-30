package com.github.gelald.current.learning.locktests;

import lombok.extern.slf4j.Slf4j;

import static com.github.gelald.current.learning.ConcurrentConstant.*;

@Slf4j
public class ObjectLockTest {
    static class ObjectLockThreadA implements Runnable {
        @Override
        public void run() {
            synchronized (LOCK) {
                for (int i = START; i <= END; i++) {
                    log.info("Thread A: {}", i);
                }
            }
        }
    }

    static class ObjectLockThreadB implements Runnable {
        @Override
        public void run() {
            synchronized (LOCK) {
                for (int i = START; i <= END; i++) {
                    log.info("Thread B: {}", i);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Thread(new ObjectLockThreadA()).start();
        // 防止线程b先获取对象锁
        Thread.sleep(BRIEFLY_SLEEP_TIME);
        new Thread(new ObjectLockThreadB()).start();
    }
}
