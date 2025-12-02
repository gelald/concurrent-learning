package com.github.gelald.visible;

public class VisibleTest {
    static boolean flag = true;

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            while (flag) {
                // 如果不加以下代码，子线程无法感知到flag而退出循环
                /*synchronized (flag) {

                }*/
                System.out.println("执行...");

                // synchronized的happens-before规则可以解决可见性问题，println方法中也是使用了synchronized关键字
                // synchronized块进入和退出时会创建内存屏障，确保变量最新值从主内存中读取
            }
        }).start();

        Thread.sleep(2000);

        flag = false;

    }
}
