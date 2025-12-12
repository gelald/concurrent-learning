package com.github.gelald.concurrent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
@RequiredArgsConstructor
@Endpoint(id = "metrics-thread-pool")
public class ThreadPoolMetricsEndpoint {
    private final ThreadPoolHolder threadPoolHolder;

    @ReadOperation
    public Map<String, Object> threadPoolMetrics() {
        ThreadPoolExecutor globalThreadPool = threadPoolHolder.getGlobalThreadPool();

        if (globalThreadPool == null) {
            log.error("Global ThreadPool is null, has not been initialized");
            return new HashMap<>();
        }

        Map<String, Object> metrics = new HashMap<>();
        // 核心线程数 （一旦创建就不销毁，但是一开始不会初始化所有的核心线程数）
        metrics.put("corePoolSize", globalThreadPool.getCorePoolSize());
        // 最大线程数 （一旦创建就无法修改）
        metrics.put("maxPoolSize", globalThreadPool.getMaximumPoolSize());
        // 当前正在工作的线程数（如果不在工作，就不纳入计算）
        metrics.put("activeThreads", globalThreadPool.getActiveCount());
        // 当前线程池实际上有多少线程
        metrics.put("poolSize", globalThreadPool.getPoolSize());

        // 当前阻塞队列还有多大的容量 （总容量 - size）
        metrics.put("queueCapacity", globalThreadPool.getQueue().remainingCapacity());
        // 当前队列有多少个任务正在排队
        metrics.put("queueSize", globalThreadPool.getQueue().size());

        return metrics;
    }
}
