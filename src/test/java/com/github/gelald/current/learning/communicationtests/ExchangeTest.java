package com.github.gelald.current.learning.communicationtests;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Exchanger;

@Slf4j
public class ExchangeTest {
    public static void main(String[] args) throws InterruptedException {
        Exchanger<String> exchanger = new Exchanger<>();

        new Thread(() -> {
            try {
                log.info("线程A得到了另一个线程的数据: {}", exchanger.exchange("这是来自线程A的数据"));
            } catch (InterruptedException e) {
                log.error("exception: ", e);
            }
        }).start();

        log.info("这时线程A是阻塞的，在等待线程B的数据");
        Thread.sleep(1000);

        new Thread(() -> {
            try {
                log.info("线程B得到了另一个线程的数据: {}", exchanger.exchange("这是来自线程B的数据"));
            } catch (InterruptedException e) {
                log.error("exception: ", e);
            }
        }).start();
    }
}
