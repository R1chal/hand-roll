package com.richal.learn.iterator;

/**
 * 聚合接口
 * 定义了创建迭代器的方法
 *
 * @param <E> 元素类型
 * @author Richal
 */
public interface Aggregate<E> {

    /**
     * 创建迭代器
     *
     * @return 迭代器对象
     */
    Iterator<E> createIterator();

    /**
     * 添加元素
     *
     * @param element 要添加的元素
     */
    void add(E element);

    /**
     * 获取集合大小
     *
     * @return 集合中元素的数量
     */
    int size();
}
