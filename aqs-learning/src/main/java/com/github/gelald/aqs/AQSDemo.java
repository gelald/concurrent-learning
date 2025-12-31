package com.github.gelald.aqs;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class AQSDemo {
    public static void main(String[] args) throws InterruptedException {
        SimpleMutex simpleMutex = new SimpleMutex();

        Thread t1 = new Thread(() -> {
            // 线程1：获取锁并持有3秒
            System.out.println(Thread.currentThread().getName() + " 尝试获取锁");
            simpleMutex.lock();
            System.out.println(Thread.currentThread().getName() + " 获取锁成功");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                System.out.println(Thread.currentThread().getName() + " 释放锁");
                simpleMutex.unlock();
            }
        }, "Thread-1");

        Thread t2 = new Thread(() -> {
            try {
                // 确保线程1拿到锁
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // 线程2：获取锁时会进入阻塞状态，直到线程1释放锁
            System.out.println(Thread.currentThread().getName() + " 尝试获取锁");
            simpleMutex.lock();
            System.out.println(Thread.currentThread().getName() + " 获取锁成功");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                simpleMutex.unlock();
                System.out.println(Thread.currentThread().getName() + " 释放锁");
            }
        }, "Thread-2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("主线程结束，锁状态：" + (simpleMutex.isLocked() ? "已锁" : "未锁"));

        ReentrantMutex reentrantMutex = new ReentrantMutex();

        Thread t = new Thread(() -> {
            System.out.println("线程开始");

            reentrantMutex.lock();
            System.out.println("第1次 lock，holdCount = " + reentrantMutex.getHoldCount());

            reentrantMutex.lock();
            System.out.println("第2次 lock，holdCount = " + reentrantMutex.getHoldCount());

            reentrantMutex.lock();
            System.out.println("第3次 lock，holdCount = " + reentrantMutex.getHoldCount());

            // 释放三次
            reentrantMutex.unlock();
            System.out.println("第1次 unlock，holdCount = " + reentrantMutex.getHoldCount());

            reentrantMutex.unlock();
            System.out.println("第2次 unlock，holdCount = " + reentrantMutex.getHoldCount());

            reentrantMutex.unlock();
            System.out.println("第3次 unlock，holdCount = " + reentrantMutex.getHoldCount());

            System.out.println("线程结束，锁是否释放: " + !reentrantMutex.isLocked());
        });

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

/**
 * 一个基于 AQS 的简单互斥锁（不可重入）
 */
class SimpleMutex {
    // 实现互斥锁的核心组件，通过 AQS 来构建同步工具
    private static class Sync extends AbstractQueuedSynchronizer {
        @Override
        protected boolean tryAcquire(int arg) {
            // 通过 CAS 的方式修改 state 值
            // 原子地获取锁
            if (compareAndSetState(0, 1)) {
                // 记录当前拥有独立访问权限的线程
                // 可重入的基础
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == 0) {
                // 如果 state 已经是 0，说明没有成功获取锁，或者已经被释放了，抛出异常
                throw new IllegalMonitorStateException();
            }
            // 清除拥有独立访问权限线程的标识
            setExclusiveOwnerThread(null);
            // state = 0 说明释放锁
            setState(0);
            return true;
        }

        @Override
        protected boolean isHeldExclusively() {
            // 通过 state 是否等于 1 来判断锁是否被持有
            return getState() == 1;
        }
    }

    private final Sync sync = new Sync();

    public void lock() {
        sync.acquire(1);
    }

    public void unlock() {
        sync.release(1);
    }

    public boolean isLocked() {
        return sync.isHeldExclusively();
    }
}

class ReentrantMutex {
    private static class Sync extends AbstractQueuedSynchronizer {
        @Override
        protected boolean tryAcquire(int acquires) {
            Thread current = Thread.currentThread();
            int state = getState();

            if (state == 0) {
                // 无锁，尝试获取
                if (compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            } else if (current == getExclusiveOwnerThread()) {
                // 可重入：同一线程再次获取
                int next = state + acquires;
                if (next < 0) { // 溢出检查
                    throw new Error("Maximum lock count exceeded");
                }
                setState(next);
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int releases) {
            int state = getState() - releases;
            if (Thread.currentThread() != getExclusiveOwnerThread()) {
                // 非持有者释放
                throw new IllegalMonitorStateException();
            }

            boolean free = false;

            if (state == 0) {
                // 重入次数归零，真正释放
                free = true;
                setExclusiveOwnerThread(null);
            }
            // 未归零，继续更新 state
            setState(state);
            // 注意：AQS release() 会检查 tryRelease() 返回值是否为 true
            return free;
        }

        @Override
        protected boolean isHeldExclusively() {
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

        public int getHoldCount() {
            // 如果被持有着，返回重入次数
            // 如果没有被持有，直接返回0
            return isHeldExclusively() ? getState() : 0;
        }
    }

    private final Sync sync = new Sync();

    public void lock() {
        sync.acquire(1);
    }

    public void unlock() {
        sync.release(1);
    }

    public boolean isLocked() {
        return sync.isHeldExclusively();
    }

    public int getHoldCount() {
        return sync.getHoldCount();
    }
}