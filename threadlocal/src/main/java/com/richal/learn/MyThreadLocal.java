package com.richal.learn;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 手写 ThreadLocal 实现（使用 ConcurrentHashMap 存储 Thread 到 ThreadLocalMap 的映射）
 *
 * 核心原理：
 * 1. 每个 Thread 对象对应一个 ThreadLocalMap，存储在全局 ConcurrentHashMap 中
 * 2. ThreadLocal 本身作为 key（弱引用），用户设置的值为 value
 * 3. 通过 Thread.currentThread() 获取当前线程，再操作其 ThreadLocalMap
 *
 * 内存泄漏防护：
 * 1. Key 使用弱引用，ThreadLocal 可被 GC 回收
 * 2. Entry 继承 WeakReference，确保 key 被回收后可以被清理
 * 3. 定期清理过期 Entry 机制
 *
 * @author Richal
 * @date 2025/03/18
 */
public class MyThreadLocal<T> {

    /**
     * 用于生成唯一的 threadLocalHashCode
     * 使用斐波那契散列乘数，使 hash 分布更均匀
     * 2^32 * 0.6180339887 ≈ 2654435769 (0x61c88647)
     */
    private static final int HASH_INCREMENT = 0x61c88647;

    /**
     * 原子计数器，用于生成初始的 nextHashCode
     */
    private static AtomicInteger nextHashCode = new AtomicInteger();

    /**
     * 每个 ThreadLocal 实例的唯一哈希码
     */
    private final int threadLocalHashCode = nextHashCode();

    /**
     * 全局存储：Thread -> ThreadLocalMap
     * 使用 ConcurrentHashMap 保证线程安全
     */
    private static final ConcurrentHashMap<Thread, MyThreadLocalMap> threadLocalMaps =
            new ConcurrentHashMap<>();

    /**
     * 生成下一个哈希码
     * 使用斐波那契散列，减少碰撞概率
     */
    private static int nextHashCode() {
        return nextHashCode.getAndAdd(HASH_INCREMENT);
    }

    /**
     * 获取当前线程的 ThreadLocalMap，如果不存在则返回 null
     */
    private MyThreadLocalMap getMap(Thread t) {
        return threadLocalMaps.get(t);
    }

    /**
     * 为当前线程创建 ThreadLocalMap 并设置初始值
     */
    private void createMap(Thread t, T firstValue) {
        MyThreadLocalMap map = new MyThreadLocalMap(this, firstValue);
        threadLocalMaps.put(t, map);
    }

    /**
     * 设置当前线程的 ThreadLocal 值
     *
     * 流程：
     * 1. 获取当前线程
     * 2. 获取线程的 ThreadLocalMap
     * 3. 如果 map 存在则设置值，不存在则创建 map
     */
    public void set(T value) {
        Thread t = Thread.currentThread();
        MyThreadLocalMap map = getMap(t);
        if (map != null) {
            map.set(this, value);
        } else {
            createMap(t, value);
        }
    }

    /**
     * 获取当前线程的 ThreadLocal 值
     *
     * 流程：
     * 1. 获取当前线程
     * 2. 获取线程的 ThreadLocalMap
     * 3. 如果 map 存在且包含 entry，则返回 value
     * 4. 否则调用 initialValue() 并设置初始值
     */
    public T get() {
        Thread t = Thread.currentThread();
        MyThreadLocalMap map = getMap(t);
        if (map != null) {
            MyThreadLocalMap.Entry e = map.getEntry(this);
            if (e != null) {
                @SuppressWarnings("unchecked")
                T result = (T) e.value;
                return result;
            }
        }
        return setInitialValue();
    }

    /**
     * 设置初始值
     * 调用 initialValue() 并将结果设置到当前线程
     */
    private T setInitialValue() {
        T value = initialValue();
        Thread t = Thread.currentThread();
        MyThreadLocalMap map = getMap(t);
        if (map != null) {
            map.set(this, value);
        } else {
            createMap(t, value);
        }
        return value;
    }

    /**
     * 移除当前线程的 ThreadLocal 值
     * 重要：调用 remove() 可防止内存泄漏
     */
    public void remove() {
        MyThreadLocalMap m = getMap(Thread.currentThread());
        if (m != null) {
            m.remove(this);
        }
    }

    /**
     * 提供初始值的默认实现
     * 子类可重写此方法
     */
    protected T initialValue() {
        return null;
    }

    /**
     * 带初始值的构造方法（Lambda 友好）
     */
    public static <S> MyThreadLocal<S> withInitial(java.util.function.Supplier<? extends S> supplier) {
        return new SuppliedThreadLocal<>(supplier);
    }

    /**
     * 使用 Supplier 提供初始值的 ThreadLocal 子类
     */
    static final class SuppliedThreadLocal<T> extends MyThreadLocal<T> {

        private final java.util.function.Supplier<? extends T> supplier;

        SuppliedThreadLocal(java.util.function.Supplier<? extends T> supplier) {
            this.supplier = supplier;
            }

        @Override
        protected T initialValue() {
            return supplier.get();
        }
    }

    /**
     * ThreadLocalMap - ThreadLocal 的核心内部类
     *
     * 设计要点：
     * 1. Entry 数组采用开放寻址法（线性探测）解决哈希冲突
     * 2. Key 是弱引用，Value 是强引用
     * 3. 自动清理过期 Entry（key 为 null）
     *
     * 内存泄漏防护：
     * - key 使用 WeakReference，当 ThreadLocal 外部无强引用时，key 可被 GC
     * - 但 value 仍是强引用，必须调用 remove() 或使用自动清理机制
     */
    static class MyThreadLocalMap {

        /**
         * 初始容量，必须是 2 的幂
         */
        private static final int INITIAL_CAPACITY = 16;

        /**
         * Entry 数组，用于存储键值对
         */
        private Entry[] table;

        /**
         * 当前存储的 Entry 数量
         */
        private int size = 0;

        /**
         * 扩容阈值 = 容量 * 2/3
         */
        private int threshold;

        /**
         * Entry 类 - 继承 WeakReference，使 key 为弱引用
         */
        static class Entry extends WeakReference<MyThreadLocal<?>> {
            /**
             * value 是强引用
             */
            Object value;

            Entry(MyThreadLocal<?> k, Object v) {
                super(k);
                value = v;
            }
        }

        /**
         * 计算扩容阈值
         */
        private void setThreshold(int len) {
            threshold = len * 2 / 3;
        }

        /**
         * 构造方法 - 延迟初始化数组
         */
        MyThreadLocalMap() {
        }

        /**
         * 构造方法 - 创建并设置第一个键值对
         */
        MyThreadLocalMap(MyThreadLocal<?> firstKey, Object firstValue) {
            table = new Entry[INITIAL_CAPACITY];
            int i = firstKey.threadLocalHashCode & (INITIAL_CAPACITY - 1);
            table[i] = new Entry(firstKey, firstValue);
            size = 1;
            setThreshold(INITIAL_CAPACITY);
        }

        /**
         * 根据 key 获取 Entry
         *
         * 流程：
         * 1. 计算哈希位置
         * 2. 如果命中则返回
         * 3. 如果 key 为 null，说明是过期 Entry，清理后返回 null
         * 4. 如果未命中，线性探测继续查找
         */
        private Entry getEntry(MyThreadLocal<?> key) {
            if (table == null) {
                return null;
            }
            int i = key.threadLocalHashCode & (table.length - 1);
            Entry e = table[i];
            if (e != null && e.get() == key) {
                return e;
            }
            return getEntryAfterMiss(key, i, e);
        }

        /**
         * 当直接哈希未命中时，线性探测查找
         *
         * 同时清理遇到的过期 Entry（key 为 null 的 Entry）
         */
        private Entry getEntryAfterMiss(MyThreadLocal<?> key, int i, Entry e) {
            Entry[] tab = table;
            int len = tab.length;

            while (e != null) {
                MyThreadLocal<?> k = e.get();
                if (k == key) {
                    return e;
                }
                // key 为 null，说明 ThreadLocal 已被回收，清理此过期 Entry
                if (k == null) {
                    expungeStaleEntry(i);
                }
                // 线性探测：i = (i + 1) % len
                i = nextIndex(i, len);
                e = tab[i];
            }
            return null;
        }

        /**
         * 设置键值对
         *
         * 流程：
         * 1. 计算哈希位置
         * 2. 线性探测查找 key 或空槽
         * 3. 如果遇到过期 Entry（key 为 null），替换它
         * 4. 如果表已满，需要扩容
         */
        private void set(MyThreadLocal<?> key, Object value) {
            // 延迟初始化
            if (table == null) {
                createTable();
            }

            Entry[] tab = table;
            int len = tab.length;
            int i = key.threadLocalHashCode & (len - 1);

            // 线性探测
            for (Entry e = tab[i]; e != null; e = tab[i = nextIndex(i, len)]) {
                MyThreadLocal<?> k = e.get();

                // 找到相同 key，更新 value
                if (k == key) {
                    e.value = value;
                    return;
                }

                // 遇到过期 Entry（key 为 null），替换它
                if (k == null) {
                    replaceStaleEntry(key, value, i);
                    return;
                }
            }

            // 找到空槽，插入新 Entry
            tab[i] = new Entry(key, value);
            int sz = ++size;

            // 清理部分过期 Entry，检查是否需要扩容
            if (!cleanSomeSlots(i, sz) && sz >= threshold) {
                rehash();
            }
        }

        /**
         * 延迟初始化 Entry 数组
         */
        private void createTable() {
            table = new Entry[INITIAL_CAPACITY];
            setThreshold(INITIAL_CAPACITY);
        }

        /**
         * 移除键值对
         */
        private void remove(MyThreadLocal<?> key) {
            if (table == null) {
                return;
            }
            Entry[] tab = table;
            int len = tab.length;
            int i = key.threadLocalHashCode & (len - 1);

            for (Entry e = tab[i]; e != null; e = tab[i = nextIndex(i, len)]) {
                if (e.get() == key) {
                    // 清除 key 的弱引用
                    e.clear();
                    // 清理此过期 Entry
                    expungeStaleEntry(i);
                    return;
                }
            }
        }

        /**
         * 替换过期 Entry
         *
         * 在索引 staleSlot 处找到一个 key 为 null 的过期 Entry，
         * 用新的 key-value 替换它，并清理附近的其他过期 Entry
         */
        private void replaceStaleEntry(MyThreadLocal<?> key, Object value, int staleSlot) {
            Entry[] tab = table;
            int len = tab.length;
            Entry e;

            // 向前扫描，找到最前面的过期 Entry
            int slotToExpunge = staleSlot;
            for (int i = prevIndex(staleSlot, len); (e = tab[i]) != null; i = prevIndex(i, len)) {
                if (e.get() == null) {
                    slotToExpunge = i;
                }
            }

            // 向后扫描，查找 key
            for (int i = nextIndex(staleSlot, len); (e = tab[i]) != null; i = nextIndex(i, len)) {
                MyThreadLocal<?> k = e.get();

                // 找到相同的 key，更新 value
                if (k == key) {
                    e.value = value;
                    // 交换过期位置
                    tab[i] = tab[staleSlot];
                    tab[staleSlot] = e;

                    // 确定清理起点
                    if (slotToExpunge == staleSlot) {
                        slotToExpunge = i;
                    }
                    cleanSomeSlots(expungeStaleEntry(slotToExpunge), len);
                    return;
                }

                // 记录过期 Entry 位置
                if (k == null && slotToExpunge == staleSlot) {
                    slotToExpunge = i;
                }
            }

            // 未找到 key，在过期位置插入新 Entry
            tab[staleSlot].value = null; // 帮助 GC
            tab[staleSlot] = new Entry(key, value);

            // 清理过期 Entry
            if (slotToExpunge != staleSlot) {
                cleanSomeSlots(expungeStaleEntry(slotToExpunge), len);
            }
        }

        /**
         * 清理单个过期 Entry
         *
         * 从 staleSlot 开始，清理连续的过期 Entry，
         * 直到遇到 null 为止
         *
         * @return 下一个需要检查的位置
         */
        private int expungeStaleEntry(int staleSlot) {
            Entry[] tab = table;
            int len = tab.length;

            // 清除指定位置的过期 Entry
            tab[staleSlot].value = null;
            tab[staleSlot] = null;
            size--;

            // 向后扫描，清理连续的过期 Entry，重新哈希未过期的 Entry
            Entry e;
            int i;
            for (i = nextIndex(staleSlot, len); (e = tab[i]) != null; i = nextIndex(i, len)) {
                MyThreadLocal<?> k = e.get();
                if (k == null) {
                    // 过期 Entry，清除
                    e.value = null;
                    tab[i] = null;
                    size--;
                } else {
                    // 未过期，重新哈希到正确位置
                    int h = k.threadLocalHashCode & (len - 1);
                    if (h != i) {
                        // 需要移动
                        tab[i] = null;
                        // 线性探测找到新位置
                        while (tab[h] != null) {
                            h = nextIndex(h, len);
                        }
                        tab[h] = e;
                    }
                }
            }
            return i;
        }

        /**
         * 清理一些过期 Entry
         *
         * 从 i 开始，对数级扫描一定数量的 slot
         * 用于在 set() 后轻度清理，避免全量扫描的性能损耗
         *
         * @return 如果清理了任何过期 Entry，返回 true
         */
        private boolean cleanSomeSlots(int i, int n) {
            boolean removed = false;
            Entry[] tab = table;
            int len = tab.length;
            do {
                i = nextIndex(i, len);
                Entry e = tab[i];
                if (e != null && e.get() == null) {
                    n = len;
                    removed = true;
                    i = expungeStaleEntry(i);
                }
            } while ((n >>>= 1) != 0);
            return removed;
        }

        /**
         * 重新哈希
         *
         * 先全量清理过期 Entry，如果仍然超过阈值，则扩容
         */
        private void rehash() {
            expungeStaleEntries();

            // 如果 size 仍然超过阈值的 3/4，则扩容
            if (size >= threshold - threshold / 4) {
                resize();
            }
        }

        /**
         * 扩容
         *
         * 容量翻倍，重新哈希所有 Entry 到新数组
         */
        private void resize() {
            Entry[] oldTab = table;
            int oldLen = oldTab.length;
            int newLen = oldLen * 2;
            Entry[] newTab = new Entry[newLen];
            int count = 0;

            for (int j = 0; j < oldLen; ++j) {
                Entry e = oldTab[j];
                if (e != null) {
                    MyThreadLocal<?> k = e.get();
                    if (k == null) {
                        // 过期 Entry，帮助 GC
                        e.value = null;
                    } else {
                        // 重新哈希
                        int h = k.threadLocalHashCode & (newLen - 1);
                        while (newTab[h] != null) {
                            h = nextIndex(h, newLen);
                        }
                        newTab[h] = e;
                        count++;
                    }
                }
            }

            setThreshold(newLen);
            size = count;
            table = newTab;
        }

        /**
         * 全量清理所有过期 Entry
         */
        private void expungeStaleEntries() {
            Entry[] tab = table;
            int len = tab.length;
            for (int j = 0; j < len; j++) {
                Entry e = tab[j];
                if (e != null && e.get() == null) {
                    expungeStaleEntry(j);
                }
            }
        }

        /**
         * 下一个索引（环形数组）
         */
        private static int nextIndex(int i, int len) {
            return ((i + 1 < len) ? i + 1 : 0);
        }

        /**
         * 上一个索引（环形数组）
         */
        private static int prevIndex(int i, int len) {
            return ((i - 1 >= 0) ? i - 1 : len - 1);
        }
    }

    /**
     * 获取当前 ThreadLocalMap 的 size（用于测试）
     */
    int getSize() {
        MyThreadLocalMap map = getMap(Thread.currentThread());
        return map != null ? map.size : 0;
    }
}