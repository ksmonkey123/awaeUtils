package ch.awae.utils.functional;

import java.util.function.Supplier;

@FunctionalInterface
public interface Function0<T> extends Supplier<T> {

    @Override
    default T get() {
        return apply();
    }

    T apply();

}
