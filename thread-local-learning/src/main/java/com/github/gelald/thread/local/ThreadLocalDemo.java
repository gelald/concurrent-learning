package com.github.gelald.thread.local;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ThreadLocalDemo {

    public static void main(String[] args) throws InterruptedException {
        // 创建多个线程模拟并发访问
        Thread thread1 = new Thread(() -> {
            SafeUserSession.setCurrentUser("User-Thread-1");
            try {
                TimeUnit.MILLISECONDS.sleep(350);
            } catch (InterruptedException e) {
                log.error("线程中断", e);
            }
            log.info("{} current user: {}", Thread.currentThread().getName(), SafeUserSession.getCurrentUser());
            SafeUserSession.clear();
        }, "Thread-1");

        Thread thread2 = new Thread(() -> {
            SafeUserSession.setCurrentUser("User-Thread-2");
            try {
                TimeUnit.MILLISECONDS.sleep(350);
            } catch (InterruptedException e) {
                log.error("线程中断", e);
            }
            log.info("{} current user: {}", Thread.currentThread().getName(), SafeUserSession.getCurrentUser());
            SafeUserSession.clear();
        }, "Thread-2");

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }


    static class SafeUserSession {
        // 直接使用 String 线程不安全
        // private static String currentUser;
        // 使用 ThreadLocal 为每个线程维护独立的变量副本
        private static final ThreadLocal<String> currentUser = new ThreadLocal<>();

        public static void setCurrentUser(String user) {
            // currentUser = user;
            currentUser.set(user);
        }

        public static String getCurrentUser() {
            // return currentUser;
            return currentUser.get();
        }

        public static void clear() {
            // currentUser = "";
            currentUser.remove(); // 清理资源，防止内存泄漏
        }
    }
}
