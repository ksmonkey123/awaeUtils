package ch.awae.utils.functional;

@FunctionalInterface
public interface Function3<A, B, C, T> {

    T apply(A a, B b, C c);

    default Function1<T3<A, B, C>, T> tupled() {
        return t -> apply(t._1, t._2, t._3);
    }

}
