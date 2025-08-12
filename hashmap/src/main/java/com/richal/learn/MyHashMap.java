package com.richal.learn;

/**
 * 手写 HashMap
 * 基于数组 + 链表实现的简易 HashMap
 *
 * @author Richal
 * @since 2025/08/12
 */
public class MyHashMap<K, V> {

    /**
     * 存储键值对的数组，每个位置存储一个链表头节点
     */
    Node<K, V>[] table = new Node[16];
    
    /**
     * 当前 HashMap 中的键值对数量
     */
    private int size = 0;

    /**
     * 添加键值对到 HashMap 中
     * 1. 如果 key 已存在，则更新 value 并返回旧值
     * 2. 如果 key 不存在，则添加新节点并返回 null
     *
     * @param key 键
     * @param value 值
     * @return 如果 key 已存在，则返回旧值；否则返回 null
     */
    public V put(K key, V value) {
        // 检查是否需要扩容
        resizeIfNecessary();
        
        // 计算 key 的哈希索引
        int keyIndex = indexOf(key);
        Node<K, V> nodeKV = table[keyIndex];
        
        // 情况1：该索引位置为空，直接创建新节点
        if (nodeKV == null) {
            table[keyIndex] = new Node<>(key, value);
            size++;
            return null;
        }
        
        // 情况2：该索引位置已有节点，需要遍历链表
        while (true) {
            // 如果找到相同的 key，更新 value 并返回旧值
            if (nodeKV.key.equals(key)) {
                V oldValue = nodeKV.value;
                nodeKV.value = value;
                return oldValue;
            }
            
            // 如果到达链表末尾，添加新节点
            if (nodeKV.next == null){
                nodeKV.next = new Node<>(key, value);
                size++;
                return null;
            }
            
            // 继续遍历链表
            nodeKV = nodeKV.next;
        }
    }

    /**
     * 根据 key 获取对应的 value
     *
     * @param key 要查找的键
     * @return 如果找到对应的 key，则返回其 value；否则返回 null
     */
    public V get(K key) {
        int keyIndex = indexOf(key);
        Node<K, V> nodeKV = table[keyIndex];
        
        // 遍历链表查找匹配的 key
        while (nodeKV != null) {
            if (nodeKV.key.equals(key)) {
                return nodeKV.value;
            }
            nodeKV = nodeKV.next;
        }
        return null;
    }

    /**
     * 从 HashMap 中删除指定 key 的键值对
     *
     * @param key 要删除的键
     * @return 如果找到并删除了键值对，则返回对应的 value；否则返回 null
     */
    public V remove(K key) {
        int keyIndex = indexOf(key);
        Node<K, V> nodeKV = table[keyIndex];
        
        // 如果该位置为空，直接返回 null
        if (nodeKV == null) {
            return null;
        }

        // 特殊情况：如果要删除的是链表的第一个节点
        if (nodeKV.key.equals(key)) {
            table[keyIndex] = nodeKV.next; // 将头指针指向下一个节点
            size--;
            return nodeKV.value;
        }

        // 一般情况：要删除的节点在链表中间或末尾
        Node<K, V> prev = nodeKV; // 记录前一个节点
        nodeKV = nodeKV.next;     // 从第二个节点开始查找
        
        while (nodeKV != null) {
            if (nodeKV.key.equals(key)) {
                prev.next = nodeKV.next; // 将前一个节点直接指向后一个节点，"跳过"当前节点
                size--;
                return nodeKV.value;
            }
            prev = nodeKV;
            nodeKV = nodeKV.next;
        }
        
        // 如果遍历完整个链表都没找到，返回 null
        return null;
    }

    /**
     * 返回 HashMap 中键值对的数量
     *
     * @return 键值对数量
     */
    public int size() {
        return this.size;
    }

    /**
     * 计算 key 在数组中的索引位置
     * 使用 key 的 hashCode 取模得到索引，并确保结果为非负数
     *
     * @param key 键
     * @return 数组索引，范围在 [0, table.length-1]
     */
    private int indexOf(Object key) {
        // 使用 Math.abs 确保索引为非负数，或者使用位运算
        return key.hashCode() & (table.length - 1);
    }

    /**
     * 检查并在必要时扩容哈希表
     * 当元素数量达到数组长度的 75% 时进行扩容，新数组大小为原来的 2 倍
     * 扩容后需要重新计算所有键值对的位置并重新分配
     */
    private void resizeIfNecessary() {
        // 如果当前元素数量小于数组长度的 75%，不需要扩容
        if (this.size < table.length * 0.75) {
            return;
        }
        
        // 创建一个新的数组，大小为原来的 2 倍
        Node<K, V>[] newTable = new Node[this.table.length * 2];
        
        // 遍历原数组中的每个桶（链表）
        for (Node<K, V> head : this.table) {
            // 如果桶为空，跳过
            if (head == null) {
                continue;
            }
            
            // 遍历链表中的每个节点
            Node<K, V> current = head;
            while (current != null) {
                // 计算节点在新数组中的索引
                int newIndex = current.key.hashCode() & (newTable.length - 1);
                
                // 情况1：新位置为空，直接放入
                if (newTable[newIndex] == null) {
                    // 保存当前节点的下一个节点
                    Node<K, V> next = current.next;
                    // 将当前节点放入新数组
                    newTable[newIndex] = current;
                    // 断开当前节点与原链表的连接
                    current.next = null;
                    // 继续处理下一个节点
                    current = next;
                } else {
                    // 情况2：新位置已有节点，采用头插法
                    // 保存当前节点的下一个节点
                    Node<K, V> next = current.next;
                    // 将当前节点插入到新位置的链表头部
                    current.next = newTable[newIndex];
                    newTable[newIndex] = current;
                    // 继续处理下一个节点
                    current = next;
                }
            }
        }
        
        // 更新哈希表数组引用
        this.table = newTable;
    }

    /**
     * 键值对节点类，形成单向链表结构
     * 用于解决哈希冲突（不同的 key 可能有相同的哈希值）
     */
    class Node<K, V> {
        K key;            // 键
        V value;          // 值
        Node<K, V> next;  // 指向下一个节点的引用，用于形成链表

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
