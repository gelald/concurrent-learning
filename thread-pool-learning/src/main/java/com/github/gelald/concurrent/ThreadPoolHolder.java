package com.github.gelald.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class ThreadPoolHolder {
    private final ThreadPoolProperties threadPoolProperties;
    // volatile 防止指令重排序，避免出现半初始化的情况
    private volatile ThreadPoolExecutor executorService;

    public ThreadPoolHolder(ThreadPoolProperties threadPoolProperties) {
        this.threadPoolProperties = threadPoolProperties;
    }

    public ThreadPoolExecutor getGlobalThreadPool() {
        // 第一层判断是否需要初始化
        if (executorService == null) {
            // 加锁保证同一时刻只有一个线程可以初始化 executorService
            synchronized (this) {
                // 二次检查防止同时抢占锁来初始化的线程进行重复初始化
                if (executorService == null) {
                    executorService = new ThreadPoolExecutor(
                            threadPoolProperties.getCoreSize(),
                            threadPoolProperties.getMaxSize(),
                            // 空闲10秒后回收非核心线程
                            10L, TimeUnit.SECONDS,
                            new ArrayBlockingQueue<>(threadPoolProperties.getQueueSize()),
                            new CustomThreadFactory("Metrics-ThreadPool"),
                            new ThreadPoolExecutor.AbortPolicy()
                    );
                }
            }
        }
        return executorService;
    }

    /**
     * 设置线程池前缀名
     */
    public static class CustomThreadFactory implements ThreadFactory {
        private final AtomicInteger sequence = new AtomicInteger(1);
        private final String prefix;

        public CustomThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(this.prefix + "-" + this.sequence.getAndIncrement());
            thread.setDaemon(false);
            thread.setPriority(Thread.NORM_PRIORITY);
            return thread;
        }
    }
}
