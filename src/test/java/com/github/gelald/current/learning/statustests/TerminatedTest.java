package com.github.gelald.current.learning.statustests;

import com.github.gelald.current.learning.ConcurrentConstant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TerminatedTest {
    public static void main(String[] args) throws InterruptedException {
        Thread a = new Thread(ConcurrentConstant::synchronizedMethod, "a");
        Thread b = new Thread(ConcurrentConstant::synchronizedMethod, "b");

        a.start();
        // 主线程等待线程a执行完成，等待的时间由线程a决定
        a.join();
        b.start();

        // 线程a固定是TERMINATED状态，因为主线程会等待线程a执行完成猴财打印这一行代码
        log.info("{}:{}", a.getName(), a.getState());

        // 可能打印RUNNABLE（进入testMethod方法，但未执行同步等待的代码）
        // 也可能打印TIMED_WAITING（进入testMethod方法，并且进入了同步等待）
        log.info("{}:{}", b.getName(), b.getState());
    }
}
