package ch.awae.utils.functional;

import java.util.function.Function;

@FunctionalInterface
public interface Function4<A, B, C, D, T> {

    T apply(A a, B b, C c, D d);

    default Function1<T4<A, B, C, D>, T> tupled() {
        return t -> apply(t._1, t._2, t._3, t._4);
    }

    default Function1<A, Function1<B, Function1<C, Function1<D, T>>>> curry() {
        return a -> b -> c -> d -> apply(a, b, c, d);
    }

    default Function1<A, Function3<B, C, D, T>> partialCurry() {
        return a -> (b, c, d) -> apply(a, b, c, d);
    }

    default Function3<B, C, D, T> partial(A a) {
        return (b, c, d) -> apply(a, b, c, d);
    }

    default <S> Function4<A, B, C, D, S> andThen(Function<? super T, ? extends S> f) {
        return (a, b, c, d) -> f.apply(apply(a, b, c, d));
    }

}
