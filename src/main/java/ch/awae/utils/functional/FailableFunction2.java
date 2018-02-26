package ch.awae.utils.functional;

/**
 * Similar to {@link java.util.function.BiFunction} but able to throw any
 * arbitrary exception.
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.0
 */
@FunctionalInterface
public interface FailableFunction2<A, B, T> {

    T apply(A a, B b) throws Throwable;

    default <S> FailableFunction2<A, B, S> andThen(FailableFunction1<T, S> f) {
        return (a, b) -> f.apply(this.apply(a, b));
    }

    default FailableFunction1<T2<A, B>, T> tupled() {
        return t -> apply(t._1, t._2);
    }

}
