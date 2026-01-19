package com.richal.learn.iterator;

import java.util.NoSuchElementException;

/**
 * 具体的聚合类 - 图书集合
 * 实现了 Aggregate 接口，用于存储图书
 *
 * @author richal
 */
public class BookShelf implements Aggregate<Book> {

    private Book[] books;
    private int last = 0; // 当前书架上的书籍数量

    /**
     * 构造函数，指定书架的最大容量
     *
     * @param maxSize 书架的最大容量
     */
    public BookShelf(int maxSize) {
        this.books = new Book[maxSize];
    }

    /**
     * 获取指定位置的书籍
     *
     * @param index 索引位置
     * @return 书籍对象
     */
    public Book getBookAt(int index) {
        return books[index];
    }

    @Override
    public void add(Book book) {
        if (last >= books.length) {
            // 扩容
            Book[] newBooks = new Book[books.length * 2];
            System.arraycopy(books, 0, newBooks, 0, books.length);
            books = newBooks;
        }
        books[last] = book;
        last++;
    }

    @Override
    public int size() {
        return last;
    }

    @Override
    public Iterator<Book> createIterator() {
        return new BookShelfIterator(this);
    }

    /**
     * 书架迭代器的具体实现
     */
    private class BookShelfIterator implements Iterator<Book> {
        private BookShelf bookShelf;
        private int index;
        private int lastReturnedIndex = -1; // 记录上次返回的元素索引，用于 remove 操作

        public BookShelfIterator(BookShelf bookShelf) {
            this.bookShelf = bookShelf;
            this.index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < bookShelf.size();
        }

        @Override
        public Book next() {
            if (!hasNext()) {
                throw new NoSuchElementException("没有更多元素了");
            }
            lastReturnedIndex = index;
            return bookShelf.getBookAt(index++);
        }

        @Override
        public void remove() {
            if (lastReturnedIndex < 0) {
                throw new IllegalStateException("必须先调用 next() 方法");
            }

            // 将后面的元素向前移动
            for (int i = lastReturnedIndex; i < bookShelf.size() - 1; i++) {
                bookShelf.books[i] = bookShelf.books[i + 1];
            }
            bookShelf.books[bookShelf.size() - 1] = null;
            bookShelf.last--;

            // 调整索引
            index = lastReturnedIndex;
            lastReturnedIndex = -1;
        }
    }
}
