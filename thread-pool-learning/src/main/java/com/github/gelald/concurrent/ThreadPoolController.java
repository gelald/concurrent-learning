package com.github.gelald.concurrent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ThreadPoolController {
    private final ThreadPoolHolder threadPoolHolder;

    /**
     * 提交少量任务，提交数量区间：[1, coreSize)，目的用于观测 coreSize、activeThreads
     */
    @PostMapping("/submit-short-tasks")
    public String submitShortTasks() throws InterruptedException {
        ThreadPoolExecutor globalThreadPool = threadPoolHolder.getGlobalThreadPool();
        int corePoolSize = globalThreadPool.getCorePoolSize();
        SecureRandom secureRandom = new SecureRandom();

        assert corePoolSize > 0;
        int taskCount = secureRandom.nextInt(1, corePoolSize);

        log.info("[submitShortTasks] it will submit {} tasks into globalThreadPool", taskCount);

        for (int i = 1; i <= taskCount; i++) {
            int finalI = i;
            globalThreadPool.submit(() -> {
                try {
                    log.info("No.{} will be kicked off", finalI);
                    TimeUnit.SECONDS.sleep(30L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    log.info("No.{} task finished", finalI);
                }
            });

            // 每隔两秒提交一个，更直观观察到变化
            TimeUnit.SECONDS.sleep(2L);
        }

        return String.format("%d tasks has been submit", taskCount);
    }

    /**
     * 提交中量任务，提交数量区间：[coreSize + 1, coreSize + 1 + remainingCapacity)，目的用于观测 queueSize、 activeThreads
     */
    @PostMapping("/submit-tall-tasks")
    public String submitTallTasks() throws InterruptedException {
        ThreadPoolExecutor globalThreadPool = threadPoolHolder.getGlobalThreadPool();
        int corePoolSize = globalThreadPool.getCorePoolSize();
        int remainingCapacity = globalThreadPool.getQueue().remainingCapacity();
        SecureRandom secureRandom = new SecureRandom();

        int taskCount = secureRandom.nextInt(corePoolSize + 1, corePoolSize + 1 + remainingCapacity);

        log.info("[submitTallTasks] it will submit {} tasks into globalThreadPool", taskCount);

        for (int i = 1; i <= taskCount; i++) {
            int finalI = i;
            globalThreadPool.submit(() -> {
                try {
                    log.info("No.{} will be kicked off", finalI);
                    TimeUnit.SECONDS.sleep(30L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    log.info("No.{} task finished", finalI);
                }
            });

            // 每隔两秒提交一个，更直观观察到变化
            TimeUnit.SECONDS.sleep(2L);
        }

        return String.format("%d tasks has been submit", taskCount);
    }

    /**
     * 提交大量任务，提交数量区间：[coreSize + 1 + remainingCapacity, maxSize + 1 + remainingCapacity)，目的用于观测 queueSize、poolSize、activeThreads
     */
    @PostMapping("/submit-grande-tasks")
    public String submitGrandeTasks() throws InterruptedException {
        ThreadPoolExecutor globalThreadPool = threadPoolHolder.getGlobalThreadPool();
        int corePoolSize = globalThreadPool.getCorePoolSize();
        int maximumPoolSize = globalThreadPool.getMaximumPoolSize();
        int remainingCapacity = globalThreadPool.getQueue().remainingCapacity();
        SecureRandom secureRandom = new SecureRandom();

        int taskCount = secureRandom.nextInt(corePoolSize + 1 + remainingCapacity, maximumPoolSize + 1 +remainingCapacity);

        log.info("[submitGrandeTasks] it will submit {} tasks into globalThreadPool", taskCount);

        for (int i = 1; i <= taskCount; i++) {
            int finalI = i;
            globalThreadPool.submit(() -> {
                try {
                    log.info("No.{} will be kicked off", finalI);
                    TimeUnit.SECONDS.sleep(30L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    log.info("No.{} task finished", finalI);
                }
            });

            // 每隔两秒提交一个，更直观观察到变化
            // 避免被核心线程执行完成，还没提交新任务导致无法触及queue上限，间隔可以再小一点
            TimeUnit.MILLISECONDS.sleep(100L);
        }

        return String.format("%d tasks has been submit", taskCount);
    }
}
