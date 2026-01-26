package com.github.gelald.aqs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SemaphoreTest {
    // 模拟停车场
    static class ParkingLot {
        private final Semaphore spaces; // 车位信号量
        private final AtomicInteger parkedCars; // 当前停车数

        public ParkingLot(int totalSpaces) {
            this.spaces = new Semaphore(totalSpaces);
            this.parkedCars = new AtomicInteger(0);
        }

        public void park(String carName) throws InterruptedException {
            System.out.println(carName + " 正在等待车位...");
            spaces.acquire(); // 获取车位（可能阻塞）
            int current = parkedCars.incrementAndGet();
            System.out.printf("%s 停入成功！当前停车数: %d/%d\n",
                    carName, current, spaces.availablePermits() + current);
        }

        public void leave(String carName) {
            System.out.println(carName + " 准备离场...");
            int current = parkedCars.decrementAndGet();
            spaces.release(); // 释放车位
            System.out.printf("%s 驶出停车场！当前停车数: %d/%d\n",
                    carName, current, spaces.availablePermits() + current);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ParkingLot lot = new ParkingLot(3); // 3个车位的小型停车场

        // 创建5辆车（模拟5个线程）
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 1; i <= 5; i++) {
            final String carName = "Car-" + i;
            executor.submit(() -> {
                try {
                    lot.park(carName); // 停车
                    // 模拟停车1-3秒
                    Thread.sleep((long)(Math.random() * 2000) + 1000);
                    lot.leave(carName); // 驶出
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            // 每0.5秒来一辆车
            Thread.sleep(500);
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        System.out.println("【停车场关闭】所有车辆已处理完毕");
    }
}
