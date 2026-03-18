package com.richal.learn;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * MyThreadLocal 测试类
 *
 * @author Richal
 * @date 2025/03/18
 */
public class MyThreadLocalTest {

    /**
     * 基本功能测试：set 和 get
     */
    @Test
    public void testBasicSetGet() {
        MyThreadLocal<String> threadLocal = new MyThreadLocal<>();

        // 初始值为 null
        assertNull(threadLocal.get());

        // 设置值
        threadLocal.set("Hello ThreadLocal");
        assertEquals("Hello ThreadLocal", threadLocal.get());

        // 覆盖设置
        threadLocal.set("Updated Value");
        assertEquals("Updated Value", threadLocal.get());
    }

    /**
     * 测试 initialValue 方法
     */
    @Test
    public void testInitialValue() {
        MyThreadLocal<Integer> threadLocal = new MyThreadLocal<Integer>() {
            @Override
            protected Integer initialValue() {
                return 42;
            }
        };

        // 未设置时返回初始值
        assertEquals(Integer.valueOf(42), threadLocal.get());

        // 设置后返回新值
        threadLocal.set(100);
        assertEquals(Integer.valueOf(100), threadLocal.get());
    }

    /**
     * 测试 withInitial 工厂方法
     */
    @Test
    public void testWithInitial() {
        MyThreadLocal<String> threadLocal = MyThreadLocal.withInitial(() -> "Default Value");

        // 未设置时返回默认值
        assertEquals("Default Value", threadLocal.get());

        // 设置后返回新值
        threadLocal.set("New Value");
        assertEquals("New Value", threadLocal.get());
    }

    /**
     * 测试 remove 方法
     */
    @Test
    public void testRemove() {
        MyThreadLocal<String> threadLocal = new MyThreadLocal<String>() {
            @Override
            protected String initialValue() {
                return "Initial";
            }
        };

        // 设置值
        threadLocal.set("Set Value");
        assertEquals("Set Value", threadLocal.get());

        // 移除后，返回初始值
        threadLocal.remove();
        assertEquals("Initial", threadLocal.get());
    }

    /**
     * 线程隔离性测试：不同线程有独立的值
     */
    @Test
    public void testThreadIsolation() throws InterruptedException {
        MyThreadLocal<Integer> threadLocal = new MyThreadLocal<>();

        threadLocal.set(100); // 主线程设置

        final int[] threadValue = new int[1];
        Thread thread = new Thread(() -> {
            // 子线程获取值，应该是 null
            Integer value = threadLocal.get();
            threadValue[0] = value != null ? value : -1;

            // 子线程设置自己的值
            threadLocal.set(200);
        });

        thread.start();
        thread.join();

        // 子线程应该没有值（或默认值）
        assertEquals(-1, threadValue[0]);

        // 主线程的值仍然是 100
        assertEquals(Integer.valueOf(100), threadLocal.get());
    }

    /**
     * 多线程并发测试
     */
    @Test
    public void testConcurrentAccess() throws InterruptedException {
        final int THREAD_COUNT = 10;
        final int ITERATIONS = 100;

        MyThreadLocal<AtomicInteger> threadLocal = new MyThreadLocal<>();
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    // 每个线程设置自己的计数器
                    AtomicInteger counter = new AtomicInteger(0);
                    threadLocal.set(counter);

                    // 累加
                    for (int j = 0; j < ITERATIONS; j++) {
                        threadLocal.get().incrementAndGet();
                    }

                    // 验证结果
                    assertEquals(ITERATIONS, threadLocal.get().get());
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await();
    }

    /**
     * 测试多个 ThreadLocal 同时使用
     */
    @Test
    public void testMultipleThreadLocals() {
        MyThreadLocal<String> local1 = new MyThreadLocal<>();
        MyThreadLocal<Integer> local2 = new MyThreadLocal<>();
        MyThreadLocal<Boolean> local3 = new MyThreadLocal<>();

        local1.set("String Value");
        local2.set(42);
        local3.set(true);

        assertEquals("String Value", local1.get());
        assertEquals(Integer.valueOf(42), local2.get());
        assertEquals(Boolean.TRUE, local3.get());
    }

    /**
     * 测试弱引用和过期 Entry 清理
     * 注意：此测试依赖 GC 行为，可能不稳定
     */
    @Test
    public void testWeakReferenceCleanup() throws InterruptedException {
        // 创建并设置 ThreadLocal
        MyThreadLocal<String> threadLocal = new MyThreadLocal<>();
        threadLocal.set("Value");

        // 获取当前大小
        int sizeBefore = threadLocal.getSize();
        assertTrue("Size should be greater than 0", sizeBefore > 0);

        // 移除引用
        threadLocal.remove();

        // 验证 remove 后大小减少
        int sizeAfter = threadLocal.getSize();
        assertTrue("Size after remove should be less than before", sizeAfter < sizeBefore);
    }

    /**
     * 测试内存泄漏场景：未调用 remove()
     * 演示如果不在 finally 中 remove() 可能导致的内存泄漏
     */
    @Test
    public void testMemoryLeakScenario() throws InterruptedException {
        // 使用 ThreadLocal 存储大对象
        MyThreadLocal<byte[]> threadLocal = new MyThreadLocal<>();

        Thread thread = new Thread(() -> {
            // 分配 1MB 内存
            byte[] largeObject = new byte[1024 * 1024];
            threadLocal.set(largeObject);

            // 业务逻辑...
            // 忘记调用 threadLocal.remove()！

            // 即使将引用设为 null，ThreadLocalMap 中仍然有强引用
            largeObject = null;
        });

        thread.start();
        thread.join();

        // 此时，虽然线程已经结束，但如果使用线程池，
        // 线程会复用，ThreadLocalMap 中的大对象将一直无法被回收！

        // 正确做法：
        // try {
        //     threadLocal.set(largeObject);
        //     // 业务逻辑
        // } finally {
        //     threadLocal.remove(); // 必须调用！
        // }
    }
}