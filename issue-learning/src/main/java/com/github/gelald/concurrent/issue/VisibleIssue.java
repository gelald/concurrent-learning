package com.github.gelald.concurrent.issue;

public class VisibleIssue {
    static volatile boolean flag = true;

    /**
     * 这个代码执行的结果是子线程持续运行，无法退出
     * <br>
     * 这是变量可见性问题，主线程修改的共享变量，子线程无法感知到变化，导致无法退出循环
     * <br>
     * 解决的思路是解决可见性问题
     * <ol>
     *     <li>为 flag 添加 volatile 关键字，使得这个变量的修改具有可见性，其他线程能实时获取最新值</li>
     *     <li>在 while 循环中添加
     *     <code>
     *     synchronized (flag) { }
     *     </code>
     *     synchronized中的happens-before规则可以解决可见性问题，synchronized块进入和退出时会创建内存屏障，确保变量最新值从主内存中读取
     *     </li>
     *     <li>在 while 循环中添加 <code>System.out.println</code> 也可以解决，println方法中也是使用了synchronized关键字添加了内存屏障</li>
     *
     * </ol>
     * <br>
     * 关键是要保证变量的修改对其他线程具有可见性，让其他线程获取到最新值
     */
    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            while (flag) {
                /*synchronized (flag) {

                }*/

                /*System.out.println("执行...");*/
            }
            // 如果不解决可见性问题，这一行无法输出
            System.out.println("退出循环");
        }).start();

        Thread.sleep(2000);

        flag = false;
    }
}
