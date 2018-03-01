package ch.awae.utils.functional;

import java.util.function.Function;

@FunctionalInterface
public interface Function3<A, B, C, T> {

    T apply(A a, B b, C c);

    default Function1<T3<A, B, C>, T> tupled() {
        return t -> apply(t._1, t._2, t._3);
    }

    default Function1<A, Function1<B, Function1<C, T>>> curry() {
        return a -> b -> c -> apply(a, b, c);
    }

    default Function1<A, Function2<B, C, T>> partialCurry() {
        return a -> (b, c) -> apply(a, b, c);
    }

    default <S> Function3<A, B, C, S> andThen(Function<? super T, ? extends S> f) {
        return (a, b, c) -> f.apply(apply(a, b, c));
    }

    default Function2<B, C, T> partial(A a) {
        return (b, c) -> apply(a, b, c);
    }

}
