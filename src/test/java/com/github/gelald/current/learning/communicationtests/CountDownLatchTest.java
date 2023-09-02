package com.github.gelald.current.learning.communicationtests;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class CountDownLatchTest {

    // CountDownLatch两种用法可以用赛跑举例
    // 1. 在正式起跑前，有倒计时，并等待裁判的发令枪，发令后才能启动
    //      相当于多个线程先做准备，进入runnable状态（start方法），然后等待（await方法）
    //      主线程会进行一个短暂休眠让子线程准备（sleep方法），然后发令（countDown方法）

    // 2. 比赛开始后，比赛要等待所有运动员都通过终点线才算结束
    //      每一个子线程完成任务后会调用countDown方法，而主线程则提前调用await方法等待各个子线程
    //      当然主线程可以给定一个合理的时间，即使有个别子线程还没完成任务也算结束了，毕竟短跑比赛不可能等待1个小时

    // 定义前置任务线程
    static class PreTaskThread implements Runnable {

        private final String task;
        private final CountDownLatch countDownLatch;

        public PreTaskThread(String task, CountDownLatch countDownLatch) {
            this.task = task;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                int random = ThreadLocalRandom.current().nextInt(1000);
                Thread.sleep(random);
                log.info("{}-任务完成", task);
                countDownLatch.countDown();
            } catch (InterruptedException e) {
                log.error("exception: ", e);
            }
        }
    }

    public static void main(String[] args) {
        // 假设有三个模块需要加载
        CountDownLatch countDownLatch = new CountDownLatch(3);

        // 主任务
        new Thread(() -> {
            try {
                log.info("等待数据加载..");
                log.info("还有{}个前置任务", countDownLatch.getCount());
                countDownLatch.await();
                log.info("数据加载完成，正式开始游戏");
            } catch (InterruptedException e) {
                log.error("exception: ", e);
            }
        }).start();

        // 前置任务
        new Thread(new PreTaskThread("加载地图数据", countDownLatch)).start();
        new Thread(new PreTaskThread("加载人物模型", countDownLatch)).start();
        new Thread(new PreTaskThread("加载背景音乐", countDownLatch)).start();
    }
}
