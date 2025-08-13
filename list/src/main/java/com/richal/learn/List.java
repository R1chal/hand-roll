package com.richal.learn;

public interface List<E> extends Iterable<E>{

    void add(E element);

    void add(E element, int index);

    E remove(int index);

    boolean remove(E element);

    E get(int index);

    E set(int index, E element);

    int size();
}
