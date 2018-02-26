package ch.awae.utils.functional;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor
public class T4<A, B, C, D> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8520537911607281060L;
    public final A _1;
    public final B _2;
    public final C _3;
    public final D _4;

    @Override
    public String toString() {
        return "(" + _1 + "," + _2 + "," + _3 + "," + _4 + ")";
    }

    public static <A, B, C, D> T4<A, B, C, D> of(A a, B b, C c, D d) {
        return new T4<A, B, C, D>(a, b, c, d);
    }

    public T2<A, T3<B, C, D>> partialCurry() {
        return new T2<>(_1, new T3<>(_2, _3, _4));
    }

    public T2<A, T2<B, T2<C, D>>> curry() {
        return new T2<>(_1, new T2<>(_2, new T2<>(_3, _4)));
    }

}
