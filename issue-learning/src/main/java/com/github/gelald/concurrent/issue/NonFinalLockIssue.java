package com.github.gelald.concurrent.issue;

public class NonFinalLockIssue {
    static Integer count = 0;

    /*static final Object lock = new Object();*/

    /**
     * 这个代码执行的结果是小于20000的，并且每次执行的结果都不一致
     * <br>
     * 其实不是可见性问题，即使加上 volatile 依然是同样的结果
     * <br>
     * count 每一次递增，都生成了一个新的对象
     * <br>
     * Java中的锁，锁的是对象，但是每一轮循环count都是不同的对象
     * <br>
     * 所以锁住的对象其实是在不断的变化，这样子同步就没有意义，和不加锁的效果是一样的
     * <br>
     * 正确的解决方案是额外定义 static final Object lock = new Object();
     * <br>
     * 关键是要保证始终锁住同一个对象，这样才能保证并发的数据安全性
     */
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                synchronized (count) {
                    count++;
                }
                /*synchronized (lock) {
                    count++;
                }*/
            }
        });
        thread.start();

        for (int i = 0; i < 10000; i++) {
            synchronized (count) {
                count++;
            }
            /*synchronized (lock) {
                count++;
            }*/
        }
        thread.join();

        System.out.println(count);
    }
}
