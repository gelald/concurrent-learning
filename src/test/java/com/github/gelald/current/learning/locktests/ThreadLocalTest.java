package com.github.gelald.current.learning.locktests;

import lombok.extern.slf4j.Slf4j;

import static com.github.gelald.current.learning.ConcurrentConstant.BRIEFLY_SLEEP_TIME;

@Slf4j
public class ThreadLocalTest {
    static class ThreadLocalThreadA implements Runnable {
        private final ThreadLocal<String> threadLocal;

        public ThreadLocalThreadA(ThreadLocal<String> threadLocal) {
            this.threadLocal = threadLocal;
        }

        @Override
        public void run() {
            threadLocal.set("A");
            try {
                Thread.sleep(BRIEFLY_SLEEP_TIME);
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
                Thread.sleep(BRIEFLY_SLEEP_TIME);
            } catch (InterruptedException e) {
                log.error("exception: ", e);
            }
            log.info("Thread B: {}", threadLocal.get());
        }
    }

    public static void main(String[] args) {
        ThreadLocal<String> threadLocal = new ThreadLocal<>();
        new Thread(new ThreadLocalThreadA(threadLocal)).start();
        new Thread(new ThreadLocalThreadB(threadLocal)).start();
    }
}
