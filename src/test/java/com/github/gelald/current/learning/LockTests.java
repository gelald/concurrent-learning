package com.github.gelald.current.learning;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class LockTests {
    private static final Object LOCK = new Object();
    private static final int START = 1;
    private static final int END = 30;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    static class ThreadA implements Runnable {
        @Override
        public void run() {
            for (int i = START; i <= END; i++) {
                log.info("Thread A: {}", i);
            }
        }
    }

    static class ThreadB implements Runnable {
        @Override
        public void run() {
            for (int i = START; i <= END; i++) {
                log.info("Thread B: {}", i);
            }
        }
    }

    @Test
    public void testNonLock() {
        new Thread(new ThreadA()).start();
        new Thread(new ThreadB()).start();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

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

    @Test
    public void testObjectLock() throws InterruptedException {
        new Thread(new ObjectLockThreadA()).start();
        // 防止线程b先获取对象锁
        Thread.sleep(10L);
        new Thread(new ObjectLockThreadB()).start();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

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

    @Test
    public void testNotify() throws InterruptedException {
        // 线程a、b交互打印
        new Thread(new NotifyThreadA()).start();
        // 防止线程b先获取对象锁
        Thread.sleep(10L);
        new Thread(new NotifyThreadB()).start();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static volatile int signal = 1;

    static class SignalThreadA implements Runnable {
        @Override
        public void run() {
            while (signal <= END) {
                if (signal % 2 == 1) {
                    log.info("Thread A: {}", signal);
                    signal++;
                }
            }
        }
    }

    static class SignalThreadB implements Runnable {
        @Override
        public void run() {
            while (signal <= END) {
                if (signal % 2 == 0) {
                    log.info("Thread B: {}", signal);
                    signal++;
                }
            }
        }
    }

    @Test
    public void testSignal() throws InterruptedException {
        new Thread(new SignalThreadA()).start();
        Thread.sleep(10L);
        new Thread(new SignalThreadB()).start();
    }

}
