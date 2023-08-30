package com.github.gelald.current.learning.locktests;

import lombok.extern.slf4j.Slf4j;

import static com.github.gelald.current.learning.ConcurrentConstant.END;
import static com.github.gelald.current.learning.ConcurrentConstant.START;

@Slf4j
public class NonLockTest {

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

    public static void main(String[] args) {
        new Thread(new ThreadA()).start();
        new Thread(new ThreadB()).start();
    }
}
