package ch.awae.utils.functional;

/**
 * Similar to {@link java.util.function.Function} but able to throw any
 * arbitrary exception.
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.0
 */
@FunctionalInterface
public interface FailableFunction1<A, T> {

    T apply(A a) throws Throwable;

    default <S> FailableFunction1<A, S> andThen(FailableFunction1<T, S> f) {
        return a -> f.apply(this.apply(a));
    }

}
