package com.github.gelald.status;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlockTest {
    public static final long BRIEFLY_SLEEP_TIME = 10L;
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
        // main线程休眠时间小于synchronizedMethod方法内的休眠时间
        Thread.sleep(BRIEFLY_SLEEP_TIME);
        b.start();

        // 线程a固定是TIMED_WAITING状态，因为主线程结束时线程a还在进行同步等待
        log.info("{}:{}", a.getName(), a.getState());

        // BLOCKED（线程a未执行完成，等待线程a执行完成释放锁进入Runnable代码块）
        log.info("{}:{}", b.getName(), b.getState());
    }
}
