package ch.awae.utils.functional;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface Function1<A, T> extends Function<A, T>, FailableFunction1<A, T> {

    @Override
    default <V> Function1<A, V> andThen(Function<? super T, ? extends V> after) {
        return a -> after.apply(apply(a));
    }

    default Function0<T> partial(A a) {
        return () -> apply(a);
    }

    @Override
    default <V> Function1<V, T> compose(Function<? super V, ? extends A> before) {
        return a -> apply(before.apply(a));
    }

    default Function0<T> compose(Supplier<? extends A> f) {
        return () -> apply(f.get());
    }

    default <V, W> Function2<V, W, T> compose(BiFunction<? super V, ? super W, ? extends A> f) {
        return (a, b) -> apply(f.apply(a, b));
    }

    default <V, W, X> Function3<V, W, X, T> compose(Function3<? super V, ? super W, ? super X, ? extends A> f) {
        return (a, b, c) -> apply(f.apply(a, b, c));
    }

    default <V, W, X, Z> Function4<V, W, X, Z, T> compose(
            Function4<? super V, ? super W, ? super X, ? super Z, ? extends A> f) {
        return (a, b, c, d) -> apply(f.apply(a, b, c, d));
    }

}
