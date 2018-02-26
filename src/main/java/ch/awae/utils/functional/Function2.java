package ch.awae.utils.functional;

import java.util.function.BiFunction;

@FunctionalInterface
public interface Function2<A, B, T> extends BiFunction<A, B, T> {

    default Function1<T2<A, B>, T> tupled() {
        return t -> apply(t._1, t._2);
    }

}
