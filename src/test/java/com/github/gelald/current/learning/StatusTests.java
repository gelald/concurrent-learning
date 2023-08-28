package com.github.gelald.current.learning;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class StatusTests {
    @Test
    public void testBlocked() throws InterruptedException {
        Thread a = new Thread(this::testMethod, "a");
        Thread b = new Thread(this::testMethod, "b");

        a.start();
        // 需要注意这里main线程休眠了1000毫秒，而testMethod()里休眠了2000毫秒
        Thread.sleep(1000L);
        b.start();

        // 线程a固定是TIMED_WAITING状态，因为主线程结束时线程a还在进行同步等待
        System.out.println(a.getName() + ":" + a.getState());

        // 可能打印BLOCKED（线程a未执行完成，等待线程a执行完成释放锁进入testMethod方法）
        // 可能打印RUNNABLE（线程a执行完成，进入了testMethod方法，还未执行到同步等待的代码或者还在等待CPU资源的分配）
        // 也可能打印TIMED_WAITING（线程a执行完成，进入了testMethod并进行同步等待）
        System.out.println(b.getName() + ":" + b.getState()); // 输出？ BLOCKED
    }

    @Test
    public void testTerminated() throws InterruptedException {
        Thread a = new Thread(this::testMethod, "a");
        Thread b = new Thread(this::testMethod, "b");

        a.start();
        // 主线程等待线程a执行完成，等待的时间由线程a决定
        a.join();
        b.start();

        // 线程a固定是TERMINATED状态，因为主线程会等待线程a执行完成猴财打印这一行代码
        System.out.println(a.getName() + ":" + a.getState());

        // 可能打印RUNNABLE（进入testMethod方法，但未执行同步等待的代码）
        // 也可能打印TIMED_WAITING（进入testMethod方法，并且进入了同步等待）
        System.out.println(b.getName() + ":" + b.getState());

    }

    @Test
    public void testTimeWaiting() throws InterruptedException {
        Thread a = new Thread(this::testMethod, "a");
        Thread b = new Thread(this::testMethod, "b");

        a.start();
        // 主线程等待线程a执行，但是等待的时间是由主线程自己决定，等待1000毫秒后，无论线程a是否执行完成，主线程都不再等待
        a.join(1000L);
        b.start();

        // 线程a固定是TIMED_WAITING状态，因为主线程结束时线程a还在进行同步等待
        System.out.println(a.getName() + ":" + a.getState());

        // 可能打印BLOCKED（线程a未执行完成，等待线程a执行完成释放锁进入testMethod方法）
        // 可能打印RUNNABLE（线程a执行完成，进入了testMethod方法，还未执行到同步等待的代码或者还在等待CPU资源的分配）
        // 也可能打印TIMED_WAITING（线程a执行完成，进入了testMethod并进行同步等待）
        System.out.println(b.getName() + ":" + b.getState());
    }

    // 同步方法争夺锁
    private synchronized void testMethod() {
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            log.error("exception: ", e);
        }
    }
}
