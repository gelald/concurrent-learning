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

    static class ThreadLocalThreadA implements Runnable {
        private final ThreadLocal<String> threadLocal;

        public ThreadLocalThreadA(ThreadLocal<String> threadLocal) {
            this.threadLocal = threadLocal;
        }

        @Override
        public void run() {
            threadLocal.set("A");
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                log.error("exception: ", e);
            }
            log.info("Thread A: {}", threadLocal.get());
        }

    }

    static class ThreadLocalThreadB implements Runnable {
        private final ThreadLocal<String> threadLocal;

        public ThreadLocalThreadB(ThreadLocal<String> threadLocal) {
            this.threadLocal = threadLocal;
        }

        @Override
        public void run() {
            threadLocal.set("B");
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                log.error("exception: ", e);
            }
            log.info("Thread B: {}", threadLocal.get());
        }
    }

    @Test
    public void testThreadLocal() {
        ThreadLocal<String> threadLocal = new ThreadLocal<>();
        new Thread(new ThreadLocalThreadA(threadLocal)).start();
        new Thread(new ThreadLocalThreadB(threadLocal)).start();
    }

}
