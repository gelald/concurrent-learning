package com.github.gelald.current.learning.statustests;

import com.github.gelald.current.learning.ConcurrentConstant;
import lombok.extern.slf4j.Slf4j;

import static com.github.gelald.current.learning.ConcurrentConstant.BRIEFLY_SLEEP_TIME;

@Slf4j
public class BlockTest {
    public static void main(String[] args) throws InterruptedException {
        Thread a = new Thread(ConcurrentConstant::synchronizedMethod, "a");
        Thread b = new Thread(ConcurrentConstant::synchronizedMethod, "b");

        a.start();
        // main线程休眠时间小于synchronizedMethod方法内的休眠时间
        Thread.sleep(BRIEFLY_SLEEP_TIME);
        b.start();

        // 线程a固定是TIMED_WAITING状态，因为主线程结束时线程a还在进行同步等待
        log.info("{}:{}", a.getName(), a.getState());

        // BLOCKED（线程a未执行完成，等待线程a执行完成释放锁进入synchronizedMethod方法）
        log.info("{}:{}", b.getName(), b.getState());
    }
}
