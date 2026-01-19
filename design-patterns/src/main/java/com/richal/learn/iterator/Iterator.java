package com.richal.learn.iterator;

/**
 * 迭代器接口
 * 定义了遍历集合元素的基本操作
 *
 * @param <E> 元素类型
 * @author richal
 */
public interface Iterator<E> {

    /**
     * 判断是否还有下一个元素
     *
     * @return 如果还有元素返回 true，否则返回 false
     */
    boolean hasNext();

    /**
     * 获取下一个元素
     *
     * @return 下一个元素
     */
    E next();

    /**
     * 移除当前元素（可选操作）
     */
    void remove();
}
