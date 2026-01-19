package com.richal.learn.iterator;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 迭代器模式测试类
 * 演示迭代器模式的使用
 *
 * @author richal
 */
public class IteratorPatternTest {

    @Test
    public void testBasicIteration() {
        System.out.println("=== 测试基本迭代功能 ===");

        // 创建书架
        BookShelf bookShelf = new BookShelf(4);

        // 添加书籍
        bookShelf.add(new Book("设计模式", "GoF", 89.0));
        bookShelf.add(new Book("Java编程思想", "Bruce Eckel", 108.0));
        bookShelf.add(new Book("深入理解Java虚拟机", "周志明", 79.0));
        bookShelf.add(new Book("Effective Java", "Joshua Bloch", 68.0));

        // 使用迭代器遍历
        Iterator<Book> iterator = bookShelf.createIterator();
        int count = 0;
        while (iterator.hasNext()) {
            Book book = iterator.next();
            System.out.println(book);
            count++;
        }

        assertEquals(4, count);
        System.out.println("共遍历了 " + count + " 本书\n");
    }

    @Test
    public void testRemoveOperation() {
        System.out.println("=== 测试删除操作 ===");

        BookShelf bookShelf = new BookShelf(3);
        bookShelf.add(new Book("设计模式", "GoF", 89.0));
        bookShelf.add(new Book("Java编程思想", "Bruce Eckel", 108.0));
        bookShelf.add(new Book("深入理解Java虚拟机", "周志明", 79.0));

        System.out.println("删除前的书籍：");
        Iterator<Book> iterator1 = bookShelf.createIterator();
        while (iterator1.hasNext()) {
            System.out.println(iterator1.next());
        }

        // 删除第二本书
        Iterator<Book> iterator2 = bookShelf.createIterator();
        iterator2.next(); // 跳过第一本
        iterator2.next(); // 获取第二本
        iterator2.remove(); // 删除第二本

        System.out.println("\n删除后的书籍：");
        Iterator<Book> iterator3 = bookShelf.createIterator();
        int count = 0;
        while (iterator3.hasNext()) {
            System.out.println(iterator3.next());
            count++;
        }

        assertEquals(2, count);
        System.out.println("删除后剩余 " + count + " 本书\n");
    }

    @Test
    public void testAutoExpansion() {
        System.out.println("=== 测试自动扩容 ===");

        // 创建初始容量为 2 的书架
        BookShelf bookShelf = new BookShelf(2);

        bookShelf.add(new Book("书籍1", "作者1", 50.0));
        bookShelf.add(new Book("书籍2", "作者2", 60.0));
        bookShelf.add(new Book("书籍3", "作者3", 70.0)); // 触发扩容
        bookShelf.add(new Book("书籍4", "作者4", 80.0));

        Iterator<Book> iterator = bookShelf.createIterator();
        int count = 0;
        while (iterator.hasNext()) {
            Book book = iterator.next();
            System.out.println(book);
            count++;
        }

        assertEquals(4, count);
        System.out.println("扩容后共有 " + count + " 本书\n");
    }

    @Test
    public void testMultipleIterators() {
        System.out.println("=== 测试多个迭代器 ===");

        BookShelf bookShelf = new BookShelf(3);
        bookShelf.add(new Book("设计模式", "GoF", 89.0));
        bookShelf.add(new Book("Java编程思想", "Bruce Eckel", 108.0));
        bookShelf.add(new Book("深入理解Java虚拟机", "周志明", 79.0));

        // 创建两个独立的迭代器
        Iterator<Book> iterator1 = bookShelf.createIterator();
        Iterator<Book> iterator2 = bookShelf.createIterator();

        System.out.println("迭代器1 - 第一本书: " + iterator1.next().getName());
        System.out.println("迭代器2 - 第一本书: " + iterator2.next().getName());
        System.out.println("迭代器1 - 第二本书: " + iterator1.next().getName());
        System.out.println("迭代器2 - 第二本书: " + iterator2.next().getName());

        assertTrue(iterator1.hasNext());
        assertTrue(iterator2.hasNext());
        System.out.println();
    }

    @Test
    public void testEmptyCollection() {
        System.out.println("=== 测试空集合 ===");

        BookShelf bookShelf = new BookShelf(5);
        Iterator<Book> iterator = bookShelf.createIterator();

        assertFalse(iterator.hasNext());
        System.out.println("空书架，没有书籍可遍历\n");
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveWithoutNext() {
        System.out.println("=== 测试未调用 next() 就调用 remove() ===");

        BookShelf bookShelf = new BookShelf(2);
        bookShelf.add(new Book("测试书籍", "测试作者", 50.0));

        Iterator<Book> iterator = bookShelf.createIterator();
        iterator.remove(); // 应该抛出 IllegalStateException
    }

    /**
     * 主方法，演示迭代器模式的完整使用场景
     */
    public static void main(String[] args) {
        System.out.println("========== 迭代器模式演示 ==========\n");

        // 创建书架
        BookShelf bookShelf = new BookShelf(5);

        // 添加书籍
        bookShelf.add(new Book("设计模式：可复用面向对象软件的基础", "GoF", 89.0));
        bookShelf.add(new Book("Java编程思想（第4版）", "Bruce Eckel", 108.0));
        bookShelf.add(new Book("深入理解Java虚拟机（第3版）", "周志明", 79.0));
        bookShelf.add(new Book("Effective Java（第3版）", "Joshua Bloch", 68.0));
        bookShelf.add(new Book("代码整洁之道", "Robert C. Martin", 59.0));

        System.out.println("书架上共有 " + bookShelf.size() + " 本书\n");

        // 使用迭代器遍历所有书籍
        System.out.println("--- 遍历所有书籍 ---");
        Iterator<Book> iterator = bookShelf.createIterator();
        while (iterator.hasNext()) {
            Book book = iterator.next();
            System.out.println(book);
        }

        // 查找价格超过 80 元的书籍
        System.out.println("\n--- 查找价格超过 80 元的书籍 ---");
        Iterator<Book> priceIterator = bookShelf.createIterator();
        while (priceIterator.hasNext()) {
            Book book = priceIterator.next();
            if (book.getPrice() > 80) {
                System.out.println(book.getName() + " - ¥" + book.getPrice());
            }
        }

        System.out.println("\n========== 演示结束 ==========");
    }
}
