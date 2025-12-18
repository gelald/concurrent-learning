package com.github.gelald.concurrent.issue;

public class NonAtomicIssue {
    static volatile Integer var = 0;
    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                var++;
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                var++;
            }
        });

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        System.out.println("最终var预期值是: 20000，实际值是: " + var);
    }
}
