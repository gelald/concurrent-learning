package com.github.gelald.sync;

public class TinySynchronizedDemo {
    public static Integer result = 0;
    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            synchronized (TinySynchronizedDemo.class) {
                for (int i = 0; i < 10000; i++) {
                    result++;
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            synchronized (TinySynchronizedDemo.class) {
                for (int i = 0; i < 10000; i++) {
                    result++;
                }
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("result: " + result);
    }
}
