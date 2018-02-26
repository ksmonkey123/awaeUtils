package ch.awae.utils.collection.mutable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * a priority queue implementation where each element has a defined priority.
 * 
 * It can either be used as a min-queue or as a max-queue. In a min-queue the
 * first element is always the one with the lowest priority value, in a
 * max-queue it is the element with the highest priority value
 * 
 * @author Andreas Wälchli
 * @version 1.2, 2015-05-10
 *
 * @param <E>
 *            the element type
 */
public class PriorityQueue<E> implements Queue<E> {

    private class QueueElement implements Comparable<QueueElement> {

        Object element;
        double priority;

        QueueElement(Object element, double priority) {
            this.element = element;
            this.priority = priority;
        }

        @Override
        public int compareTo(QueueElement o) {
            return (PriorityQueue.this.isMinQueue ? 1 : -1) * Double.compare(this.priority, o.priority);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PriorityQueue.QueueElement))
                return false;
            return Objects.equals(this.element, ((PriorityQueue<?>.QueueElement) obj).element);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.element);
        }
    }

    /**
     * creates a new max-priority-queue
     * 
     * @param <T>
     *            the type of the queue
     * @return the created queue
     * @since 1.2
     */
    public static <T> PriorityQueue<T> maxQueue() {
        return new PriorityQueue<>(false);
    }

    /**
     * creates a new min-priority-queue
     * 
     * @param <T>
     *            the type of the queue
     * @return the created queue
     * @since 1.2
     */
    public static <T> PriorityQueue<T> minQueue() {
        return new PriorityQueue<>(true);
    }

    private java.util.PriorityQueue<QueueElement> backer;

    final boolean isMinQueue;

    /**
     * instantiates a new priority queue
     * 
     * @param isMinQueue
     *            {@code true} if the queue should be a min queue, {@code false}
     *            if it should be a max queue
     */
    protected PriorityQueue(boolean isMinQueue) {
        this.isMinQueue = isMinQueue;
        this.backer = new java.util.PriorityQueue<>();
    }

    @Override
    public boolean add(E e) {
        return this.backer.add(new QueueElement(e, this.isMinQueue ? Double.MAX_VALUE : Double.MIN_VALUE));
    }

    /**
     * Adds a new element with a defined priority
     * 
     * @param element
     *            the element
     * @param priority
     *            the priority
     */
    public void add(E element, double priority) {
        this.backer.add(new QueueElement(element, priority));
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return c.stream().map(this::add).reduce(false, (a, b) -> a || b);
    }

    @Override
    public void clear() {
        this.backer.clear();
    }

    @Override
    public boolean contains(Object o) {
        return this.backer.contains(new QueueElement(o, 0));
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return c.stream().allMatch(this::contains);
    }

    @SuppressWarnings("unchecked")
    @Override
    public E element() {
        return (E) this.backer.element().element;
    }

    @Override
    public boolean isEmpty() {
        return this.backer.isEmpty();
    }

    /**
     * indicates if the queue has a max-queue configuration
     * 
     * @return {@code true} iff the queue is a max-queue
     * @since 1.2
     */
    public boolean isMaxQueue() {
        return !this.isMinQueue;
    }

    /**
     * indicates if the queue has a min-queue configuration
     * 
     * @return {@code true} iff the queue is a min-queue
     * @since 1.2
     */
    public boolean isMinQueue() {
        return this.isMinQueue;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            @SuppressWarnings("synthetic-access")
            Iterator<QueueElement> backedIterator = PriorityQueue.this.backer.iterator();

            @Override
            public boolean hasNext() {
                return this.backedIterator.hasNext();
            }

            @SuppressWarnings("unchecked")
            @Override
            public E next() {
                return (E) this.backedIterator.next().element;
            }
        };
    }

    @Override
    public boolean offer(E e) {
        return this.backer.offer(new QueueElement(e, 0));
    }

    @SuppressWarnings("unchecked")
    @Override
    public E peek() {
        return (E) this.backer.peek().element;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E poll() {
        return (E) this.backer.poll().element;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E remove() {
        return (E) this.backer.remove().element;
    }

    @Override
    public boolean remove(Object o) {
        return this.backer.remove(new QueueElement(o, 0));
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return c.stream().map(this::remove).reduce(false, (a, b) -> a || b);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.backer.retainAll(c.stream().map(e -> new QueueElement(e, 0)).collect(Collectors.toList()));
    }

    /**
     * Sets the priority of an element in the queue
     * 
     * @param element
     *            the element to change the priority for
     * @param priority
     *            the new priority of the element
     */
    public void setPriority(E element, double priority) {
        QueueElement e = new QueueElement(element, priority);
        this.backer.remove(e);
        this.backer.add(e);
    }

    @Override
    public int size() {
        return this.backer.size();
    }

    @Override
    public Object[] toArray() {
        return this.backer.stream().map(e -> e.element).toArray();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        return (T[]) Arrays.copyOf(this.toArray(), this.size(), a.getClass());
    }

}
