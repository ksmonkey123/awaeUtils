package ch.awae.utils.functional;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor
public class T3<A, B, C> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 670163575740835707L;
    public final A _1;
    public final B _2;
    public final C _3;

    @Override
    public String toString() {
        return "(" + _1 + "," + _2 + "," + _3 + ")";
    }

    public static <A, B, C> T3<A, B, C> of(A a, B b, C c) {
        return new T3<>(a, b, c);
    }

    public T2<A, T2<B, C>> curry() {
        return new T2<>(_1, new T2<>(_2, _3));
    }

    public <D> T4<A, B, C, D> and(D d) {
        return T4.of(_1, _2, _3, d);
    }

}
