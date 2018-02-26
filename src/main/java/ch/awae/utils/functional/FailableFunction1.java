package ch.awae.utils.functional;

/**
 * Similar to {@link java.util.function.Function} but able to throw any
 * arbitrary exception.
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.0
 */
@FunctionalInterface
public interface FailableFunction1<A, B> {

    B apply(A a) throws Throwable;

    default <C> FailableFunction1<A, C> andThen(FailableFunction1<B, C> f) {
        return a -> f.apply(this.apply(a));
    }

}
