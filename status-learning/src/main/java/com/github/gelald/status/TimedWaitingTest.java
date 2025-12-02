package com.github.gelald.status;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimedWaitingTest {
    public static final long BRIEFLY_SLEEP_TIME = 2010L;
    public static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread a = new Thread(() -> {
            synchronized (lock) {
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    log.error("exception: ", e);
                }
            }
        }, "a");
        Thread b = new Thread(() -> {
            synchronized (lock) {
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    log.error("exception: ", e);
                }
            }
        }, "b");

        a.start();
        // 主线程等待线程a执行，但是等待的时间是由主线程自己决定，等待结束后，无论线程a是否执行完成，主线程都不再等待
        a.join(BRIEFLY_SLEEP_TIME);
        b.start();

        // 线程a固定是TIMED_WAITING状态，因为主线程结束时线程a还在进行同步等待
        log.info("{}:{}", a.getName(), a.getState());

        // 可能打印BLOCKED（线程a未执行完成，等待线程a执行完成释放锁进入Runnable代码块）
        // 可能打印RUNNABLE（线程a执行完成，进入了Runnable代码块，还未执行到同步等待的代码或者还在等待CPU资源的分配）
        // 也可能打印TIMED_WAITING（线程a执行完成，进入了Runnable代码块并进行同步等待 sleep）
        log.info("{}:{}", b.getName(), b.getState());
    }
}
