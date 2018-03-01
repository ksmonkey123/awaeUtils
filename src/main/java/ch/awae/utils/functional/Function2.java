package ch.awae.utils.functional;

import java.util.function.BiFunction;
import java.util.function.Function;

@FunctionalInterface
public interface Function2<A, B, T> extends BiFunction<A, B, T> {

    @Override
    default <V> Function2<A, B, V> andThen(Function<? super T, ? extends V> after) {
        return (a, b) -> after.apply(apply(a, b));
    }

    default Function1<A, Function1<B, T>> curry() {
        return a -> b -> apply(a, b);
    }

    default Function1<T2<A, B>, T> tupled() {
        return t -> apply(t._1, t._2);
    }

    default Function1<B, T> partial(A a) {
        return b -> apply(a, b);
    }

}
