package com.richal.learn;

import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义线程池
 */
public class MyThreadPool {

    // --- 线程池核心参数 ---
    private final int corePoolSize;
    private final int maximumPoolSize;
    private final long keepAliveTime;
    private final TimeUnit unit;
    public final BlockingQueue<Runnable> workQueue;
    private final RejectHandle rejectHandle;
    private final ThreadFactory threadFactory;

    // --- 内部状态 ---
    /**
     * 用于存放工作线程的集合
     */
    private final HashSet<Worker> workers = new HashSet<>();

    /**
     * 原子地记录当前工作线程的数量
     */
    private final AtomicInteger workerCount = new AtomicInteger(0);

    /**
     * 构造函数，用于初始化线程池
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime   非核心线程的空闲存活时间
     * @param unit            存活时间的单位
     * @param workQueue       任务队列
     * @param rejectHandle    拒绝策略
     * @param threadFactory   线程工厂
     */
    public MyThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectHandle rejectHandle, ThreadFactory threadFactory) {
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.unit = unit;
        this.workQueue = workQueue;
        this.rejectHandle = rejectHandle;
        this.threadFactory = threadFactory;
    }

    /**
     * 提交任务到线程池
     * @param command 要执行的任务
     */
    public void execute(Runnable command) {
        if (command == null) {
            throw new NullPointerException();
        }

        // 1. 如果当前线程数小于核心线程数，直接创建新线程执行任务。
        if (workerCount.get() < corePoolSize) {
            if (addWorker(command, true)) {
                return;
            }
        }

        // 2. 尝试将任务添加到队列中。
        if (workQueue.offer(command)) {
            // 添加成功，不需要额外操作。工作线程会自动来取。
        } else {
            // 3. 如果队列已满，尝试创建非核心线程（“救急线程”）来执行任务。
            if (!addWorker(command, false)) {
                // 4. 如果非核心线程也无法创建（达到最大线程数），则执行拒绝策略。
                reject(command);
            }
        }
    }

    private void reject(Runnable command) {
        rejectHandle.reject(command, this);
    }

    /**
     * 添加一个新的工作线程
     * @param firstTask 这个新线程的第一个任务，可以为null
     * @param core      标记是核心线程还是非核心线程
     * @return true如果添加成功
     */
    private boolean addWorker(Runnable firstTask, boolean core) {
        for (;;) {
            int count = workerCount.get();
            int cap = core ? corePoolSize : maximumPoolSize;
            if (count >= cap) {
                return false;
            }
            // 使用CAS原子地增加线程计数
            if (workerCount.compareAndSet(count, count + 1)) {
                Worker worker = new Worker(firstTask);
                Thread t = threadFactory.newThread(worker);
                // 加锁以安全地将工作线程添加到集合中
                synchronized (workers) {
                    workers.add(worker);
                }
                t.start();
                return true;
            }
        }
    }

    /**
     * 工作线程退出时的清理工作
     */
    private void processWorkerExit(Worker worker) {
        // 加锁以安全地移除
        synchronized (workers) {
            workers.remove(worker);
        }
        workerCount.decrementAndGet();
    }

    /**
     * 从任务队列中获取任务
     */
    private Runnable getTask() {
        // 当线程数超过核心线程数时，需要进行超时控制
        boolean timed = workerCount.get() > corePoolSize;
        try {
            if (timed) {
                // 非核心线程或多余的线程，在指定时间内获取不到任务则返回null，使其退出
                return workQueue.poll(keepAliveTime, unit);
            } else {
                // 核心线程，无限期阻塞等待任务
                return workQueue.take();
            }
        } catch (InterruptedException e) {
            // 线程被中断，也应该退出
            return null;
        }
    }

    /**
     * 工作线程的实现
     */
    private final class Worker implements Runnable {
        private Runnable firstTask;

        Worker(Runnable firstTask) {
            this.firstTask = firstTask;
        }

        @Override
        public void run() {
            Runnable task = this.firstTask;
            this.firstTask = null; // 释放引用，帮助GC

            try {
                // 循环地从队列中获取并执行任务
                while (task != null || (task = getTask()) != null) {
                    try {
                        task.run();
                    } finally {
                        task = null; // 完成一个任务后，清空任务引用
                    }
                }
            } finally {
                // 当getTask()返回null时，线程会退出循环并执行这里的清理工作
                processWorkerExit(this);
            }
        }
    }
}
