package ch.awae.utils.collection.mutable;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * This is a very basic linked queue implementation without any real
 * synchronisation.
 *
 * @author Andreas Wälchli
 * @version 1.2, 2015-05-10
 * 
 * @param <E>
 *            the element type for this queue
 */
public final class LinkedQueue<E> implements Queue<E> {

    private class Node<T> {
        T item;
        Node<T> next;

        Node(T item) {
            this.item = item;
        }
    }

    private Node<E> head = null;
    private Node<E> tail = null;
    private int size = 0;

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public boolean contains(Object o) {
        Iterator<E> it = this.iterator();
        while (it.hasNext()) {
            E item = it.next();
            if (item == null && o == null)
                return true;
            if (item == null)
                continue;
            if (item.equals(o))
                return true;
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new QueueIterator<E>(this.head);
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[this.size];
        Iterator<E> it = this.iterator();
        for (int i = 0; i < array.length && it.hasNext(); i++)
            array[i] = it.next();
        return array;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        T[] array = (T[]) Array.newInstance(a.getClass().getComponentType(), this.size);
        Iterator<E> it = this.iterator();
        for (int i = 0; i < array.length && it.hasNext(); i++)
            array[i] = (T) it.next();
        return array;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object object : c) {
            if (!this.contains(object))
                return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for (E e : c) {
            this.add(e);
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        this.size = 0;
        this.tail = this.head = null;
    }

    @Override
    public boolean add(E e) {
        if (this.head == null) {
            this.tail = this.head = new Node<>(e);
        } else {
            Node<E> node = new Node<>(e);
            this.tail.next = node;
            this.tail = node;
        }
        this.size++;
        return true;
    }

    @Override
    public boolean offer(E e) {
        return this.add(e);
    }

    @Override
    public E remove() {
        if (this.size == 0)
            throw new IllegalStateException("Queue is empty");
        return this.poll();
    }

    @Override
    public E poll() {
        if (this.size == 0)
            return null;
        E result = this.head.item;
        if (this.size > 1)
            this.head = this.head.next;
        else
            this.head = this.tail = null;
        this.size--;
        return result;
    }

    @Override
    public E element() {
        if (this.size == 0)
            throw new IllegalStateException("Queue s empty");
        return this.peek();
    }

    @Override
    public E peek() {
        return this.size > 0 ? this.head.item : null;
    }

    private class QueueIterator<T> implements Iterator<T> {

        private Node<T> currentNode;

        QueueIterator(Node<T> head) {
            this.currentNode = new Node<>(null);
            this.currentNode.next = head;
        }

        @Override
        public synchronized boolean hasNext() {
            return this.currentNode.next != null;
        }

        @Override
        public synchronized T next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return (this.currentNode = this.currentNode.next).item;
        }

    }

}
