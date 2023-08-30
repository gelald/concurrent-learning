package com.github.gelald.current.learning.locktests;

import lombok.extern.slf4j.Slf4j;

import static com.github.gelald.current.learning.ConcurrentConstant.*;

@Slf4j
public class NotifyLockTest {
    static class NotifyThreadA implements Runnable {
        @Override
        public void run() {
            synchronized (LOCK) {
                for (int i = START; i <= END; i++) {
                    try {
                        log.info("Thread A: {}", i);
                        LOCK.notify();
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        log.error("exception: ", e);
                    }
                }
                LOCK.notify();
            }
        }
    }

    static class NotifyThreadB implements Runnable {
        @Override
        public void run() {
            synchronized (LOCK) {
                for (int i = START; i <= END; i++) {
                    try {
                        log.info("Thread B: {}", i);
                        LOCK.notify();
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        log.error("exception: ", e);
                    }
                }
                LOCK.notify();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 线程a、b交互打印
        new Thread(new NotifyThreadA()).start();
        // 防止线程b先获取对象锁
        Thread.sleep(BRIEFLY_SLEEP_TIME);
        new Thread(new NotifyThreadB()).start();
    }
}
