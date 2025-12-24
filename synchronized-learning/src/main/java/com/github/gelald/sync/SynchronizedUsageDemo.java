package com.github.gelald.sync;

public class SynchronizedUsageDemo implements Runnable {
    public final Object lock = new Object();

    @Override
    public void run() {
        // 在 synchronized 中指定一个 class 对象，锁住的对象就是这个 class 对象
        /*synchronized(SynchronizedUsageDemo.class){
            System.out.println("线程 " + Thread.currentThread().getName() + " 开始");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("线程 " + Thread.currentThread().getName() + " 结束");
        }*/

        // staticMethod();

        // 在 synchronized 中指定的对象就是锁住的对象
        /*synchronized (lock) {
            System.out.println("线程 " + Thread.currentThread().getName() + " 开始");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("线程 " + Thread.currentThread().getName() + " 结束");
        }*/

         this.method();
    }

    /**
     * synchronized 用在静态方法上，锁对象是当前方法所在的 Class 对象
     */
    public static synchronized void staticMethod() {
        System.out.println("线程 " + Thread.currentThread().getName() + " 开始");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("线程 " + Thread.currentThread().getName() + " 结束");
    }

    /**
     * synchronized 用在普通方法上，锁对象是 this 指针
     */
    public synchronized void method() {
        System.out.println("线程 " + Thread.currentThread().getName() + " 开始");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("线程 " + Thread.currentThread().getName() + " 结束");
    }

    public static void main(String[] args) {
        // Thread thread1 = new Thread(new SynchronizedUsageDemo());
        // Thread thread2 = new Thread(new SynchronizedUsageDemo());

        // 因为需要演示对象锁，如果两个对象不一致，锁住的对象也就不一致，无法正确控制同步
        SynchronizedUsageDemo instance = new SynchronizedUsageDemo();
        Thread thread1 = new Thread(instance);
        Thread thread2 = new Thread(instance);

        thread1.start();
        thread2.start();
    }
}
