package com.github.gelald.thread.local;

import java.util.concurrent.TimeUnit;

public class FailedScenarios {
    public static void main(String[] args) throws InterruptedException {
        // ThreadLocal<String> parentValue = new ThreadLocal<>();
        ThreadLocal<String> parentValue = new InheritableThreadLocal<>();

        parentValue.set("测试父子线程 ThreadLocal 表现");

        System.out.println("父线程中的值: " + parentValue.get());

        Thread thread = new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(350);
                System.out.println("子线程中的值: " + parentValue.get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        thread.start();
        thread.join();

        System.out.println("结束");
    }
}
