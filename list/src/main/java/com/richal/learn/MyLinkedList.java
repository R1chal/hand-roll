package com.richal.learn;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 自定义双向链表实现，提供基本列表操作功能
 * 包括添加、删除、获取元素等操作
 * @param <E> 链表中存储的元素类型
 */
public class MyLinkedList<E> implements List<E> {

    /**
     * 链表中实际元素的数量
     */
    int size;

    /**
     * 链表的头节点
     */
    private Node<E> head;

    /**
     * 链表的尾节点
     */
    private Node<E> tail;

    /**
     * 在链表末尾添加元素
     * 时间复杂度：O(1)
     * @param element 要添加的元素
     */
    @Override
    public void add(E element) {
        // 创建新节点，prev指向当前尾节点，next为null
        Node<E> node = new Node<>(tail, element, null);
        if (tail != null) {
            // 如果链表不为空，将当前尾节点的next指向新节点
            tail.next = node;
        } else {
            // 如果链表为空，新节点同时是头节点
            head = node;
        }
        // 更新尾节点为新节点
        tail = node;
        size++;
    }

    /**
     * 在指定位置插入元素
     * 时间复杂度：O(n)，需要遍历找到插入位置
     * @param element 要插入的元素
     * @param index 插入位置的索引
     */
    @Override
    public void add(E element, int index) {
        // 检查索引是否有效
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        // 如果在末尾插入，调用尾部添加方法
        if (index == size) {
            add(element);
            return;
        }
        // 找到要插入位置的节点
        Node<E> indexNode = findNode(index);
        Node<E> pre = indexNode.prev;
        // 创建新节点，连接前后节点
        Node<E> node = new Node<>(pre, element, indexNode);
        if (pre == null) {
            // 如果在头部插入，更新头节点
            head = node;
        } else {
            // 否则，更新前节点的next引用
            pre.next = node;
        }
        // 更新后节点的prev引用
        indexNode.prev = node;
        size++;
    }

    /**
     * 根据索引查找节点
     * 优化查找效率：根据索引位置决定从头部或尾部开始查找
     * 时间复杂度：O(n)，但平均只需要遍历一半的节点
     * @param index 要查找的索引位置
     * @return 对应索引位置的节点
     */
    private Node<E> findNode(int index) {
        Node<E> node = null;
        if (index < size / 2) {
            // 如果索引在前半部分，从头开始查找
            node = head;
            for (int i = 0; i < index; i++) {
                node = node.next;
            }
        } else {
            // 如果索引在后半部分，从尾开始查找
            node = tail;
            for (int i = size - 1; i > index; i--) {
                node = node.prev;
            }
        }
        return node;
    }

    /**
     * 删除指定位置的元素
     * 时间复杂度：O(n)，需要查找到要删除的节点
     * @param index 要删除元素的索引
     * @return 被删除的元素
     */
    @Override
    public E remove(int index) {
        // 检查索引是否有效
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        // 找到要删除的节点
        Node<E> node = findNode(index);
        Node<E> prev = node.prev;
        Node<E> next = node.next;
        
        // 更新前节点的next引用
        if (prev == null) {
            // 如果删除头节点，更新头节点
            head = next;
        } else {
            prev.next = next;
        }

        // 更新后节点的prev引用
        if (next == null) {
            // 如果删除尾节点，更新尾节点
            tail = prev;
        } else {
            next.prev = prev;
        }
        
        // 清除被删除节点的引用，帮助GC
        node.prev = null;
        node.next = null;
        size--;
        return node.element;
    }

    /**
     * 删除第一个与指定元素相等的元素
     * 时间复杂度：O(n)，需要遍历查找元素
     * @param element 要删除的元素
     * @return 如果找到并删除了元素则返回true，否则返回false
     */
    @Override
    public boolean remove(E element) {
        if (size == 0) {
            return false;
        }
        
        // 从头开始遍历查找元素
        Node<E> current = head;
        while (current != null) {
            // 比较元素是否相等，考虑null值的情况
            if ((element == null && current.element == null) ||
                    (element != null && element.equals(current.element))) {
                
                Node<E> prev = current.prev;
                Node<E> next = current.next;
                
                // 更新前节点的next引用
                if (prev == null) {
                    head = next;
                } else {
                    prev.next = next;
                }
                
                // 更新后节点的prev引用
                if (next == null) {
                    tail = prev;
                } else {
                    next.prev = prev;
                }
                
                // 清除被删除节点的引用，帮助GC
                current.prev = null;
                current.next = null;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * 获取指定位置的元素
     * 时间复杂度：O(n)，需要查找到对应位置的节点
     * @param index 元素的索引
     * @return 指定位置的元素
     */
    @Override
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return findNode(index).element;
    }

    /**
     * 修改指定位置的元素
     * 时间复杂度：O(n)，需要查找到对应位置的节点
     * @param index 要修改的元素索引
     * @param element 新元素值
     * @return 被替换的旧元素
     */
    @Override
    public E set(int index, E element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node<E> node = findNode(index);
        E oldElement = node.element;
        node.element = element;
        return oldElement;
    }

    /**
     * 获取链表中元素的数量
     * 时间复杂度：O(1)
     * @return 链表大小
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * 返回一个迭代器，用于遍历链表中的元素
     * @return 链表迭代器
     */
    @Override
    public Iterator<E> iterator() {
        return new LinkedListIterator();
    }

    /**
     * 内部迭代器实现，用于遍历链表元素
     */
    class LinkedListIterator implements Iterator<E> {

        /**
         * 当前迭代位置的节点，初始为头节点
         */
        Node<E> current = head;

        /**
         * 检查是否还有下一个元素
         * @return 如果有下一个元素则返回true，否则返回false
         */
        @Override
        public boolean hasNext() {
            return current != null;
        }

        /**
         * 获取下一个元素并前进迭代器
         * @return 下一个元素
         * @throws NoSuchElementException 如果没有更多元素
         */
        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            E element = current.element;
            current = current.next;
            return element;
        }
    }

    /**
     * 链表节点内部类，存储元素和前后节点的引用
     * @param <E> 节点中存储的元素类型
     */
    class Node<E> {

        /**
         * 节点中存储的元素
         */
        private E element;
        
        /**
         * 指向下一个节点的引用
         */
        private Node<E> next;
        
        /**
         * 指向前一个节点的引用
         */
        private Node<E> prev;

        /**
         * 创建只包含元素的节点
         * @param element 节点中存储的元素
         */
        public Node(E element) {
            this.element = element;
        }

        /**
         * 创建包含前后节点引用的完整节点
         * @param prev 前一个节点
         * @param element 节点中存储的元素
         * @param next 后一个节点
         */
        public Node(Node<E> prev, E element, Node<E> next) {
            this.element = element;
            this.prev = prev;
            this.next = next;
        }
    }
}
