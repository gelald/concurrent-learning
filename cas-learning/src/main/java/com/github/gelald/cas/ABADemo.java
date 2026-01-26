package com.github.gelald.cas;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicStampedReference;

public class ABADemo {
    @Getter
    static class Account {
        private int balance;
        public Account(int balance) { this.balance = balance; }
        public void withdraw(int amount) { balance -= amount; }
        public void deposit(int amount) { balance += amount; }
    }

    public static void main(String[] args) throws InterruptedException {
        // 1. 有问题的 CAS
        Account account = new Account(100);

        // 线程A：挂起前读取余额=100
        int expected = account.getBalance();

        // 线程B：修改余额
        Thread thread1 = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            account.withdraw(50); // 100→50
            account.deposit(50);  // 50→100
        });
        thread1.start();

        thread1.join();

        // 线程A：恢复后 CAS
        if (account.getBalance() == expected) { // true! 但状态已变化
            account.withdraw(50); // 错误扣除！
        }
        System.out.println("错误结果: " + account.getBalance()); // 50 (应为100)

        // 2. 修复：AtomicStampedReference
        AtomicStampedReference<Account> safeAccount =
                new AtomicStampedReference<>(new Account(100), 0);

        int[] stampHolder = new int[1];
        Account expectedAcc = safeAccount.get(stampHolder);
        int stamp = stampHolder[0];

        Thread thread2 = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            // 模拟状态变化
            Account newAcc = new Account(50);
            safeAccount.compareAndSet(expectedAcc, newAcc, stamp, stamp + 1);
            safeAccount.compareAndSet(newAcc, new Account(100), stamp + 1, stamp + 2);
        });
        thread2.start();

        thread2.join();

        // 线程A：CAS 检查版本号
        Account newAcc = new Account(50);
        boolean success = safeAccount.compareAndSet(
                expectedAcc, newAcc, stamp, stamp+1
        );
        System.out.println("修复结果: " + success); // false! 版本号已变
    }
}
