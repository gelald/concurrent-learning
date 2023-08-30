package com.github.gelald.current.learning.communicationtests;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.gelald.current.learning.ConcurrentConstant.END;
import static com.github.gelald.current.learning.ConcurrentConstant.START;

@Slf4j
public class SemaphoreTest {
    static class SemaphoreThread implements Runnable {

        private final int value;
        private final Semaphore semaphore;

        public SemaphoreThread(int value, Semaphore semaphore) {
            this.value = value;
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            try {
                // 申请permit
                semaphore.acquire();
                log.info("当前线程是{}, 还剩下{}个资源, 还有{}个线程在等待",
                        value, semaphore.availablePermits(), semaphore.getQueueLength());
                // 睡眠随机时间，打乱释放顺序
                Thread.sleep(ThreadLocalRandom.current().nextLong(1000));
                log.info("线程{}释放了资源", value);
            } catch (InterruptedException e) {
                log.error("exception: ", e);
            } finally {
                // 释放permit
                semaphore.release(); // 释放permit
            }
        }
    }

    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(3);
        for (int i = START; i <= END; i++) {
            new Thread(new SemaphoreThread(i, semaphore)).start();
        }
    }
}
