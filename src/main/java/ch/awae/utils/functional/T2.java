package ch.awae.utils.functional;

import java.util.Objects;

/**
 * Immutable generic 2-Tuple
 * 
 * @author Andreas Wälchli
 * @version 1.1, 2015-05-09
 *
 * @param <A>
 *            the type of the first element
 * @param <B>
 *            the type or the second element
 */
public class T2<A, B> {

    /**
     * The first element
     */
    public final A _1;
    /**
     * The second element
     */
    public final B _2;

    /**
     * Creates a new tuple instance
     * 
     * @param _1
     *            the first element
     * @param _2
     *            the second element
     */
    public T2(A _1, B _2) {
        this._1 = _1;
        this._2 = _2;
    }

    public static <A, B> T2<A, B> of(A a, B b) {
        return new T2<>(a, b);
    }

    /**
     * Returns a tuple with the same elements as this one, but in the reverse
     * order
     * 
     * @return a reversed version of this tuple
     */
    public T2<B, A> flip() {
        return new T2<>(this._2, this._1);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || !(obj instanceof T2))
            return false;
        T2<?, ?> other = (T2<?, ?>) obj;
        return Objects.equals(this._1, other._1) && Objects.equals(this._2, other._2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this._1, this._2);
    }

    @Override
    public String toString() {
        return "(" + this._1 + "," + this._2 + ")";
    }
}
