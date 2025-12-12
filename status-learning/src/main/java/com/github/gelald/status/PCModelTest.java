package com.github.gelald.status;

import org.springframework.util.CollectionUtils;

import java.util.concurrent.ArrayBlockingQueue;

public class PCModelTest {
    public static final Integer queueSize = 10;

    public static final ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(queueSize);

    public static void main(String[] args) {
        Consumer consumer = new Consumer();
        consumer.start();

        for (int i = 0; i < 10; i++) {
            Producer producer = new Producer();
            producer.start();
        }
    }

    static class Producer extends Thread {
        @Override
        public void run() {
            // 生产者每次工作前，都应该锁住queue，防止读写竞争
            synchronized (queue) {
                // 如果队列已经满了，装不下更多数据了
                if (queue.size() == queueSize) {
                    // 执行wait，避免CPU空转浪费资源，让出锁
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        // 这里的 notify 是为了确保线程中断时能正确释放锁并允许其他线程继续执行
                        queue.notify();
                    }
                } else {
                    // 如果队列还有位置，那么就生成消息
                    queue.add(queue.size() + 1);

                    System.out.println("生产者往队列加入消息，当前队列长度" + queue.size());

                    // 通知消费者开始工作
                    queue.notify();

                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    static class Consumer extends Thread {
        @Override
        public void run() {
            // 消费者需要重复工作
            while (true) {
                // 消费者每次工作前，都应该锁住queue，防止读写竞争
                synchronized (queue) {
                    // 如果队列没有数据
                    if (CollectionUtils.isEmpty(queue)) {
                        System.out.println("消费者发现当前队列为空，进入睡眠");
                        // 执行wait，避免CPU空转浪费资源，让出锁
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            // 这里的 notify 是为了确保线程中断时能正确释放锁并允许其他线程继续执行
                            queue.notify();
                        }
                    } else {
                        // 如果队列中有数据，取出数据执行逻辑
                        Integer data = queue.poll();

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        System.out.println("消费者处理数据：" + data);

                        queue.notify();
                    }
                }
            }
        }
    }
}
