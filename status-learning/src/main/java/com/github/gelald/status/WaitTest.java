package com.github.gelald.status;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WaitTest {
    static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread a = new Thread(() -> {
            synchronized (lock) {
                try {
                    lock.wait(2000L);
                } catch (InterruptedException e) {
                    log.error("exception: ", e);
                }
            }
        });
        a.setName("Thread-A");

        a.start();
        Thread.sleep(10L);

        // 线程a固定是TIMED_WAITING状态，因为主线程结束时线程a还在进行同步等待
        log.info("{}:{}", a.getName(), a.getState());

//        synchronized (lock) {
//            lock.notify();
//        }

        Thread b = new Thread(() -> {
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                log.error("exception: ", e);
            }
        });
        b.setName("Thread-B");

        b.start();
        Thread.sleep(10L);

        // 线程b固定是TIMED_WAITING状态，因为主线程结束时线程a还在进行同步等待
        log.info("{}:{}", b.getName(), b.getState());


        Thread c = new Thread(() -> {
            Thread.yield();
        });
        c.setName("Thread-C");

        c.start();
        Thread.sleep(10L);

        // 线程c不一定会让出CPU，yield只是一个信号
        log.info("{}:{}", c.getName(), c.getState());

    }
}
