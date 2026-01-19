package com.richal.learn;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 线程池测试类
 *
 * @author Richal
 * @since 2025/08/10
 */
public class ThreadPoolTest {

    @Test
    @DisplayName("测试线程池基本功能 - 任务执行和线程管理")
    public void testThreadPoolBasicFunctionality() throws InterruptedException {
        // 创建线程池
        MyThreadPool myThreadPool = new MyThreadPool(
                2,  // 核心线程数
                4,  // 最大线程数
                1,  // 空闲线程存活时间
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2),  // 任务队列容量为2
                new DiscardRejectHandle(),    // 拒绝策略
                new ThreadFactory() {
                    private int threadNumber = 1;
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r, "MyPool-Thread-" + threadNumber++);
                        t.setDaemon(false);
                        return t;
                    }
                }
        );

        // 用于统计执行完成的任务数
        AtomicInteger completedTasks = new AtomicInteger(0);

        // 提交5个任务
        for (int i = 0; i < 5; i++) {
            myThreadPool.execute(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName());
                completedTasks.incrementAndGet();
            });
        }

        System.out.println("主线程没有被阻塞");

        // 等待所有任务完成（最多等待6秒）
        Thread.sleep(6000);

        // 验证至少有一些任务被执行了
        // 注意：由于使用了DiscardRejectHandle，部分任务可能被丢弃
        assertTrue(completedTasks.get() > 0, "至少应该有一些任务被执行");
        System.out.println("完成的任务数: " + completedTasks.get());
    }

    @Test
    @DisplayName("测试线程池拒绝策略")
    public void testThreadPoolRejectPolicy() throws InterruptedException {
        // 创建一个容量很小的线程池
        MyThreadPool myThreadPool = new MyThreadPool(
                1,  // 核心线程数
                1,  // 最大线程数
                1,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1),  // 队列容量为1
                new DiscardRejectHandle(),
                new ThreadFactory() {
                    private int threadNumber = 1;
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "TestPool-" + threadNumber++);
                    }
                }
        );

        AtomicInteger executedTasks = new AtomicInteger(0);

        // 提交多个任务，超过线程池和队列的容量
        for (int i = 0; i < 5; i++) {
            myThreadPool.execute(() -> {
                try {
                    Thread.sleep(500);
                    executedTasks.incrementAndGet();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        // 等待任务执行
        Thread.sleep(3000);

        // 由于使用了DiscardRejectHandle，部分任务会被丢弃
        // 只有核心线程数+队列容量的任务会被执行
        assertTrue(executedTasks.get() <= 2, "执行的任务数应该不超过核心线程数+队列容量");
        System.out.println("实际执行的任务数: " + executedTasks.get());
    }
}
