package ch.awae.utils.functional;

import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface Function0<T> extends Supplier<T>, FailableFunction0<T> {

    @Override
    default T get() {
        return apply();
    }

    T apply();

    default <S> Function0<S> andThen(Function<? super T, ? extends S> f) {
        return () -> f.apply(get());
    }

}
