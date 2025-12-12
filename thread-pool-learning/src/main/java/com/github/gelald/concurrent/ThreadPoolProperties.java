package com.github.gelald.concurrent;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "global.thread-pool")
public class ThreadPoolProperties {
    /**
     * 线程池核心线程数
     */
    private Integer coreSize = 5;
    /**
     * 线程池队列容量
     */
    private Integer queueSize = 50;
    /**
     * 线程池最大线程数
     */
    private Integer maxSize = 10;
}
