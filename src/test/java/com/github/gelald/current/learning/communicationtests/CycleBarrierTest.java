package com.github.gelald.current.learning.communicationtests;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class CycleBarrierTest {

    static class PreTaskThread implements Runnable {

        private final String task;
        private final CyclicBarrier cyclicBarrier;

        public PreTaskThread(String task, CyclicBarrier cyclicBarrier) {
            this.task = task;
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public void run() {
            // 假设总共5个关卡
            for (int i = 1; i < 6; i++) {
                try {
                    int random = ThreadLocalRandom.current().nextInt(1000);
                    Thread.sleep(random);
                    log.info("{}-任务完成", task);
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    log.error("exception: ", e);
                }
            }
        }
    }

    public static void main(String[] args) {

        CyclicBarrier cyclicBarrier = new CyclicBarrier(
                3, // 定义每一个关卡总共需要做多少步前置工作
                // 所有前置工作都做好后，可以执行下面的逻辑
                () -> log.info("本关卡所有前置任务完成，开始游戏...")
        );

        new Thread(new PreTaskThread("加载地图数据", cyclicBarrier)).start();
        new Thread(new PreTaskThread("加载人物模型", cyclicBarrier)).start();
        new Thread(new PreTaskThread("加载背景音乐", cyclicBarrier)).start();
    }
}
