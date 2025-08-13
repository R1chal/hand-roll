package com.richal.learn;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LockTest {

    @Test
    public void testLock() throws InterruptedException {
        // 创建锁实例
        final MyLock lock = new MyLock();
        
        // 共享计数器
        final AtomicInteger counter = new AtomicInteger(0);
        
        // 线程数量
        final int threadCount = 10;
        
        // 每个线程的操作次数
        final int operationsPerThread = 1000;
        
        // 用于等待所有线程准备就绪
        final CountDownLatch readyLatch = new CountDownLatch(threadCount);
        
        // 用于等待所有线程完成
        final CountDownLatch doneLatch = new CountDownLatch(threadCount);
        
        // 创建并启动多个线程
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                // 线程准备就绪
                readyLatch.countDown();
                try {
                    // 等待所有线程准备就绪
                    readyLatch.await();
                    
                    // 每个线程重复执行增加计数器的操作
                    for (int j = 0; j < operationsPerThread; j++) {
                        // 获取锁
                        lock.lock();
                        try {
                            // 模拟随机工作负载
                            if (j % 10 == 0) {
                                Thread.sleep(1);
                            }
                            
                            // 增加计数器（临界区操作）
                            int current = counter.get();
                            counter.set(current + 1);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        } finally {
                            // 释放锁
                            lock.unlock();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    // 标记线程完成
                    doneLatch.countDown();
                }
            });
            thread.start();
        }
        
        // 等待所有线程完成，最多等待30秒
        boolean completed = doneLatch.await(30, TimeUnit.SECONDS);
        
        // 验证所有线程是否完成
        assertTrue("测试超时，有线程未能完成", completed);
        
        // 验证计数器的最终值是否符合预期
        assertEquals("计数器值不符合预期", threadCount * operationsPerThread, counter.get());
        
        System.out.println("测试通过：" + threadCount + "个线程，每个执行" + 
                          operationsPerThread + "次操作，计数器最终值为" + counter.get());
    }
    
    @Test
    public void testLockContention() throws InterruptedException {
        // 创建锁实例
        final MyLock lock = new MyLock();
        
        // 测试锁在高竞争情况下的表现
        final int threadCount = 20;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(threadCount);
        
        // 创建线程，所有线程同时竞争锁
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            Thread thread = new Thread(() -> {
                try {
                    // 等待统一开始信号
                    startLatch.await();
                    
                    // 获取锁
                    lock.lock();
                    try {
                        // 持有锁一段时间
                        System.out.println("线程 " + threadId + " 获取到锁");
                        Thread.sleep(10);
                    } finally {
                        lock.unlock();
                        System.out.println("线程 " + threadId + " 释放了锁");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
            thread.start();
        }
        
        // 发出开始信号，所有线程同时竞争锁
        startLatch.countDown();
        
        // 等待所有线程完成
        boolean completed = doneLatch.await(10, TimeUnit.SECONDS);
        assertTrue("测试超时，有线程未能完成", completed);
        
        System.out.println("锁竞争测试完成");
    }
} 