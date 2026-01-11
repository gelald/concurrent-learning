package com.github.gelald.aqs;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PCModelDemo {
    public static void main(String[] args) throws InterruptedException {
         WaitNotifyModel model = new WaitNotifyModel();
//        ConditionLockModel model = new ConditionLockModel();

        // 2个生产者，3个消费者
        Thread p1 = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                try {
                    model.produce(i);
                    Thread.sleep(100); // 模拟处理时间
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Producer-1");

        Thread p2 = new Thread(() -> {
            for (int i = 6; i <= 10; i++) {
                try {
                    model.produce(i);
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Producer-2");

        Thread c1 = new Thread(() -> {
            for (int i = 0; i < 4; i++) {
                try {
                    model.consume();
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Consumer-1");

        Thread c2 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                try {
                    model.consume();
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Consumer-2");

        Thread c3 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                try {
                    model.consume();
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Consumer-3");

        System.out.println("===== 启动 synchronized + wait/notify 测试 =====");
//        System.out.println("===== 启动 Lock + Condition 测试 =====");
        c1.start(); c2.start(); c3.start();
        p1.start(); p2.start();

        // 等待所有线程完成
        p1.join(); p2.join();
        c1.join(); c2.join(); c3.join();
        System.out.println("===== synchronized 测试完成 =====\n");
//        System.out.println("===== Lock 测试完成 =====\n");
    }
}

class WaitNotifyModel {
    private final Queue<Integer> queue = new LinkedList<>();
    private final int capacity = 5;

    public synchronized void produce(int item) throws InterruptedException {
        while (queue.size() == capacity) {
            System.out.println(Thread.currentThread().getName() + " [producer] 队列已满，不能继续添加元素，生产者释放锁");
            wait(); // 1. 释放锁 2. 进入锁对象 Monitor 的 waitSet
        }
        queue.add(item);
        System.out.println(Thread.currentThread().getName() + " [producer] 队列添加元素 " + item + ", 元素数量=" + queue.size() + ", 唤醒消费者");
        notifyAll(); // 唤醒获取锁的线程（包括所有消费者和同样等待锁的生产者！）
    }

    public synchronized int consume() throws InterruptedException {
        while (queue.isEmpty()) {
            System.out.println(Thread.currentThread().getName() + " [consumer] 队列是空的，等待生产者添加元素，消费者释放锁");
            wait(); // 1. 释放锁 2. 进入锁对象 Monitor 的 waitSet
        }
        int item = queue.poll();
        System.out.println(Thread.currentThread().getName() + " [consumer] 从队列中取出元素 " + item + ", 元素数量=" + queue.size() + ", 唤醒生产者");
        notifyAll(); // 唤醒获取锁的线程（包括所有生产者和同样等待锁的消费者！）
        return item;
    }
}

class ConditionLockModel {
    private final Queue<Integer> queue = new LinkedList<>();
    private final int capacity = 5;

    private final Lock lock = new ReentrantLock();
    // 两个独立条件队列
    private final Condition notFull = lock.newCondition();  // 队列不满
    private final Condition notEmpty = lock.newCondition(); // 队列非空

    public void produce(int item) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                System.out.println(Thread.currentThread().getName() + " [producer] 队列已满，不能继续添加元素，生产者释放锁");
                notFull.await(); // 1. 释放锁 2. 进入 notFull 条件队列
            }
            queue.add(item);
            System.out.println(Thread.currentThread().getName() + " [producer] 队列添加元素 " + item + ", 元素数量=" + queue.size() + ", 唤醒消费者");
            notEmpty.signal(); // 仅唤醒 notEmpty 队列（所有消费者）
        } finally {
            lock.unlock();
        }
    }

    public int consume() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                System.out.println(Thread.currentThread().getName() + " [consumer] 队列是空的，等待生产者添加元素，消费者释放锁");
                notEmpty.await(); // 1. 释放锁 2. 进入 notEmpty 条件队列
            }
            int item = queue.poll();
            System.out.println(Thread.currentThread().getName() + " [consumer] 从队列中取出元素 " + item + ", 元素数量=" + queue.size() + ", 唤醒生产者");
            notFull.signal(); // 仅唤醒 notFull 队列（所有生产者）
            return item;
        } finally {
            lock.unlock();
        }
    }
}