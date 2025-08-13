package com.richal.learn;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

/**
 * 自定义锁实现，基于AQS思想的简化版本
 * 使用原子变量和等待队列实现线程安全的锁机制
 */
public class MyLock {

    /**
     * 表示锁的状态，false表示未锁定，true表示已锁定
     * 使用AtomicBoolean保证状态变更的原子性
     */
    AtomicBoolean flag = new AtomicBoolean(false);

    /**
     * 当前持有锁的线程，用于验证解锁操作的合法性
     */
    Thread owner = null;

    /**
     * 等待队列的头节点，使用AtomicReference保证线程安全
     * 初始化为一个哨兵节点，简化队列操作的边界条件处理
     */
    AtomicReference<Node> head = new AtomicReference<>(new Node());

    /**
     * 等待队列的尾节点，使用AtomicReference保证线程安全
     * 初始时与头节点指向同一个哨兵节点
     */
    AtomicReference<Node> tail = new AtomicReference<>(head.get());

    /**
     * 尝试获取锁，如果锁已被占用则将当前线程加入等待队列并阻塞
     * 实现了非公平锁的语义，新到达的线程可能会抢占等待中的线程
     */
    public void lock() {
        // 快速路径：尝试直接获取锁
        if (flag.compareAndSet(false, true)) {
            owner = Thread.currentThread();
            return;
        }
        
        // 创建代表当前线程的节点
        Node current = new Node();
        current.thread = Thread.currentThread();
        
        // 将当前线程节点添加到等待队列尾部
        // 使用CAS操作确保在并发情况下正确添加
        while (true) {
            Node currentTail = tail.get();
            if (tail.compareAndSet(currentTail, current)) {
                // 成功添加到队列尾部后，设置前后引用关系
                current.pre = currentTail;
                currentTail.next = current;
                break;
            }
        }
        
        // 自旋等待获取锁
        while (true) {
            // 阻塞当前线程，等待被唤醒
            LockSupport.park();
            
            // 被唤醒后，检查是否轮到自己获取锁
            // 只有当自己是队列中的第一个等待节点，并且锁处于可用状态时才能获取锁
            if (current.pre == head.get() && flag.compareAndSet(false, true)) {
                // 获取锁成功，更新锁的拥有者
                owner = Thread.currentThread();
                // 更新队列头节点为当前节点
                head.set(current);
                // 清理前节点的引用，帮助GC
                current.pre.next = null;
                current.pre = null;
                return;
            }
        }
    }

    /**
     * 释放锁，并唤醒等待队列中的下一个线程
     * 只有锁的拥有者才能释放锁
     */
    public void unlock() {
        // 验证当前线程是否为锁的拥有者
        if (Thread.currentThread() != this.owner) {
            throw new IllegalStateException("当前线程没有锁，不能解锁");
        }

        // 获取当前头节点和下一个等待的节点
        Node headNode = head.get();
        Node next = headNode.next;
        
        // 清除锁的拥有者
        owner = null;
        
        // 释放锁
        flag.set(false);
        
        // 如果有等待的线程，唤醒它
        if (next != null) {
            LockSupport.unpark(next.thread);
        }
    }

    /**
     * 等待队列的节点类，表示等待获取锁的线程
     */
    class Node {
        /**
         * 前一个节点的引用
         */
        Node pre;
        
        /**
         * 后一个节点的引用
         */
        Node next;
        
        /**
         * 该节点对应的线程
         */
        Thread thread;
    }
}
