package com.github.gelald.lock;

import lombok.extern.slf4j.Slf4j;

import static com.github.gelald.lock.ConcurrentConstant.*;

@Slf4j
public class ObjectLockTest {
    public static void main(String[] args) throws InterruptedException {
        // 直接用锁的方式，只能保证线程A输出完，线程B才输出，无法保证交替的效果
        new Thread(new ObjectLockThreadA()).start();
        // 防止线程b先获取对象锁
        Thread.sleep(BRIEFLY_SLEEP_TIME);
        new Thread(new ObjectLockThreadB()).start();
    }

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

}
