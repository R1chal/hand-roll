package com.richal.learn;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 手搓线程池
 *
 * @author Richal
 * @since 2025/08/10
 */
public class Main {
    public static void main(String[] args) {
        MyThreadPool myThreadPool = new MyThreadPool(2, 4, 1,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2),
                new DiscardRejectHandle(),
                new ThreadFactory() {
                    private int threadNumber = 1;
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r, "MyPool-Thread-" + threadNumber++);
                        t.setDaemon(false);
                        return t;
                    }
                });
        for (int i = 0; i < 5; i++) {
            myThreadPool.execute(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName());
            });
        }
        System.out.println("主线程没有被阻塞");
    }
}