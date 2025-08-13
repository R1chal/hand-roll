package com.richal.learn;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * 自定义ArrayList实现，基于数组实现的动态列表
 * 提供了基本列表操作功能，包括添加、删除、获取元素等
 * @param <E> 列表中存储的元素类型
 */
public class MyArrayList<E> implements List<E> {

    /**
     * 底层存储数据的数组，初始容量为10
     */
    Object[] table = new Object[10];

    /**
     * 列表中实际元素的数量
     */
    private int size;

    /**
     * 在列表末尾添加元素
     * 时间复杂度：平均O(1)，最坏O(n)当需要扩容时
     * @param element 要添加的元素
     */
    @Override
    public void add(E element) {
        // 检查是否需要扩容
        if (size == table.length) {
            resize();
        }
        // 在末尾添加元素并增加size
        table[size++] = element;
    }

    /**
     * 扩容数组，将容量翻倍
     * 时间复杂度：O(n)，需要复制所有元素
     */
    private void resize() {
        Object[] newTable = new Object[size * 2];
        System.arraycopy(table, 0, newTable, 0, size);
        table = newTable;
    }

    /**
     * 在指定位置插入元素
     * 时间复杂度：O(n)，需要移动插入位置后的所有元素
     * @param element 要插入的元素
     * @param index 插入位置的索引
     */
    @Override
    public void add(E element, int index) {
        // 检查索引是否有效
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        // 检查是否需要扩容
        if (size == table.length) {
            resize();
        }
        // 将插入位置及之后的元素向后移动一位
        System.arraycopy(table, index, table, index + 1, size - index);
        // 在指定位置插入元素
        table[index] = element;
    }

    /**
     * 删除指定位置的元素
     * 时间复杂度：O(n)，需要移动删除位置后的所有元素
     * @param index 要删除元素的索引
     * @return 被删除的元素
     */
    @Override
    public E remove(int index) {
        // 检查索引是否有效
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        // 保存要删除的元素
        E removed = (E) table[index];
        // 将删除位置之后的元素向前移动一位
        System.arraycopy(table, index + 1, table, index, size - index);
        size--;
        // 清除最后一个位置的引用，帮助GC
        table[size] = null;
        return removed;
    }

    /**
     * 删除第一个与指定元素相等的元素
     * 时间复杂度：O(n)，需要遍历查找元素
     * @param element 要删除的元素
     * @return 如果找到并删除了元素则返回true，否则返回false
     */
    @Override
    public boolean remove(E element) {
        // 遍历查找元素
        for (int i = 0; i < size; i++) {
            if (Objects.equals(element, table[i])) {
                // 找到后调用按索引删除的方法
                remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * 获取指定位置的元素
     * 时间复杂度：O(1)
     * @param index 元素的索引
     * @return 指定位置的元素
     */
    @Override
    public E get(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        return (E) table[index];
    }

    /**
     * 修改指定位置的元素
     * 时间复杂度：O(1)
     * @param index 要修改的元素索引
     * @param element 新元素值
     * @return 被替换的旧元素
     */
    @Override
    public E set(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        E old = (E) table[index];
        table[index] = element;
        return old;
    }

    /**
     * 获取列表中元素的数量
     * 时间复杂度：O(1)
     * @return 列表大小
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * 返回一个迭代器，用于遍历列表中的元素
     * @return 列表迭代器
     */
    @Override
    public Iterator<E> iterator() {
        return new MyIterator();
    }

    /**
     * 内部迭代器实现，用于遍历列表元素
     */
    class MyIterator implements Iterator<E> {

        /**
         * 当前迭代位置
         */
        int cursor;

        /**
         * 检查是否还有下一个元素
         * @return 如果有下一个元素则返回true，否则返回false
         */
        @Override
        public boolean hasNext() {
            return cursor != size;
        }

        /**
         * 获取下一个元素并前进迭代器
         * @return 下一个元素
         * @throws NoSuchElementException 如果没有更多元素
         */
        @Override
        public E next() {
            if (cursor >= size) {
                throw new NoSuchElementException();
            }
            E element = (E) table[cursor];
            cursor++;
            return element;
        }
    }
}
