package ch.awae.utils.collection.immutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Immutable Single-Linked List.
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.1
 *
 * @param <T>
 *            the element type for the list
 */
public class List<T> implements Iterable<T>, Serializable {

    private static final long serialVersionUID = 8338929059809007954L;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private final static List EMPTY_LIST = new List(null, null);

    /**
     * Provides the empty list for any type argument
     * 
     * @return the empty list
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> empty() {
        return EMPTY_LIST;
    }

    /**
     * Creates a new list from an array of elements
     * 
     * @param ts
     *            the elements to create a list for
     * @return the list containing the elements
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> List<T> of(T... ts) {
        List<T> list = EMPTY_LIST;
        for (int i = ts.length - 1; i >= 0; i--)
            list = new List<>(ts[i], list);
        return list;
    }

    /**
     * Creates a new list from a given collection
     * 
     * @param collection
     *            the collection to convert to a list
     * @return a list containing the elements of the collection
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> from(Collection<T> collection) {
        Object[] array = collection.toArray(new Object[0]);
        return (List<T>) of(array);
    }

    private final int size;
    private final T head;
    private final List<? extends T> tail;

    /**
     * Creates a new list from a head and a tail.
     * 
     * The head of a list is the first element, the tail is another list that
     * holds the remaining elements. The list ends with a special empty element
     * 
     * @param head
     *            the head of the list
     * @param tail
     *            the tail. a {@code null} tail is interpreted as the empty
     *            list.
     */
    @SuppressWarnings("unchecked")
    public List(T head, List<? extends T> tail) {
        this.head = head;
        if (head != null && tail == null)
            this.tail = EMPTY_LIST;
        else
            this.tail = tail;
        if (isEmpty())
            size = 0;
        else
            size = 1 + tail.size;
    }

    /**
     * Indicates if this is the empty list
     */
    public boolean isEmpty() {
        return tail == null;
    }

    /**
     * Returns the head of the list
     * 
     * @return the head
     * @throws NoSuchElementException
     *             this is the empty list
     */
    public T head() {
        if (isEmpty())
            throw new NoSuchElementException();
        return head;
    }

    /**
     * Returns the tail of the list
     * 
     * @return the tail
     * @throws NoSuchElementException
     *             this is the empty list
     */
    public List<? extends T> tail() {
        if (isEmpty())
            throw new NoSuchElementException();
        return tail;
    }

    /**
     * Creates a new List with the provided element as the head and this list as
     * the tail
     * 
     * @param element
     *            the element to prepend
     * @return a new list
     */
    public List<T> prepend(T element) {
        return new List<>(element, this);
    }

    /**
     * the size of the list
     */
    public int size() {
        return size;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((head == null) ? 0 : head.hashCode());
        result = prime * result + ((tail == null) ? 0 : tail.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof List)) {
            return false;
        }
        List<?> other = (List<?>) obj;
        if (head == null) {
            if (other.head != null) {
                return false;
            }
        } else if (!head.equals(other.head)) {
            return false;
        }
        if (tail == null) {
            if (other.tail != null) {
                return false;
            }
        } else if (!tail.equals(other.tail)) {
            return false;
        }
        return true;
    }

    /**
     * Checks if this list contains a given element
     * 
     * @param o
     *            the element to search
     * @return {@code true} iff the element exists
     */
    public boolean contains(Object o) {
        if (Objects.equals(head, o))
            return true;
        else if (isEmpty())
            return false;
        else
            return tail.contains(o);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("List(");
        boolean first = true;
        for (T elem : this) {
            if (!first)
                sb.append(',');
            sb.append(elem.toString());
        }
        sb.append(')');
        return sb.toString();
    }

    /**
     * Retrieves the element at a given index
     * 
     * @return the element
     * @throws IndexOutOfBoundsException
     *             the index is out of bounds
     */
    public T get(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("index out of bounds: " + index);
        List<? extends T> list = this;
        while (index-- > 0)
            list = list.tail;
        return list.head;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            List<? extends T> head = List.this;

            @Override
            public boolean hasNext() {
                return !head.isEmpty();
            }

            @Override
            public T next() {
                if (head.isEmpty())
                    throw new NoSuchElementException();
                else {
                    T elem = head.head;
                    head = head.tail;
                    return elem;
                }
            }
        };
    }

}
