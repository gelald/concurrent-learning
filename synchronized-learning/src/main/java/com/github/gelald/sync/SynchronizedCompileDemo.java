package com.github.gelald.sync;

import org.openjdk.jol.info.ClassLayout;

public class SynchronizedCompileDemo {
    private static final Object lock = new Object();

    public synchronized void method() {
        System.out.println("synchronized 修饰方法");
    }

    public void codeBlock() {
        synchronized (this) {
            System.out.println("synchronized 修饰代码块");
        }
    }

    // javap -c -s -v -l SynchronizedCompileDemo.class

    public static void main(String[] args) {
        System.out.println("初始状态:\n" + ClassLayout.parseInstance(lock).toPrintable());

        synchronized(lock) {
            System.out.println("加锁后:\n" + ClassLayout.parseInstance(lock).toPrintable());
        }

        System.out.println("释放锁:\n" + ClassLayout.parseInstance(lock).toPrintable());
    }
}
