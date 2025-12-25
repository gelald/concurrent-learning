package com.github.gelald.sync;

public class SynchronizedCompileDemo {
    public synchronized void method() {
        System.out.println("synchronized 修饰方法");
    }

    public void codeBlock() {
        synchronized (this) {
            System.out.println("synchronized 修饰代码块");
        }
    }

    // javap -c -s -v -l SynchronizedCompileDemo.class
}
