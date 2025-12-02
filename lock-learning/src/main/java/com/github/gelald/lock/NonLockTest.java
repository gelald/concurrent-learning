package com.github.gelald.lock;

import lombok.extern.slf4j.Slf4j;

import static com.github.gelald.lock.ConcurrentConstant.END;
import static com.github.gelald.lock.ConcurrentConstant.START;

@Slf4j
public class NonLockTest {
    public static void main(String[] args) {
        // 没有任何包含措施，无法保证线程A、B交替输出
        new Thread(new ThreadA()).start();
        new Thread(new ThreadB()).start();
    }

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
}
