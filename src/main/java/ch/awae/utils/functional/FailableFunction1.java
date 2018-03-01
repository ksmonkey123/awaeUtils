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

    default <S> FailableFunction1<A, S> andThen(FailableFunction1<? super T, ? extends S> f) {
        return a -> f.apply(this.apply(a));
    }

    default <V> FailableFunction1<V, T> compose(FailableFunction1<? super V, ? extends A> f) {
        return a -> apply(f.apply(a));
    }

    default <V> FailableFunction0<T> compose(FailableFunction0<? extends A> f) {
        return () -> apply(f.apply());
    }

    default FailableFunction0<T> partial(A a) {
        return () -> apply(a);
    }

}
