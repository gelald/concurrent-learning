package com.github.gelald.lock;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConcurrentConstant {
    public static final Object LOCK = new Object();
    public static final int START = 1;
    public static final int END = 80;
    public static final long BRIEFLY_SLEEP_TIME = 10L;

    public static synchronized void synchronizedMethod() {
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            log.error("exception: ", e);
        }
    }
}
