package ch.awae.utils.functional;

@FunctionalInterface
public interface Function4<A, B, C, D, T> {

    T apply(A a, B b, C c, D d);

    default Function1<T4<A, B, C, D>, T> tupled() {
        return t -> apply(t._1, t._2, t._3, t._4);
    }

}
