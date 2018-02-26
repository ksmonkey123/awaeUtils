package ch.awae.utils.functional;

/**
 * Similar to {@link java.util.function.Function} but able to throw any
 * arbitrary exception.
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.0
 */
@FunctionalInterface
public interface FailableFunction3<A, B, C, T> {

    T apply(A a, B b, C c) throws Throwable;

    default <S> FailableFunction3<A, B, C, S> andThen(FailableFunction1<T, S> f) {
        return (a, b, c) -> f.apply(this.apply(a, b, c));
    }

    default FailableFunction1<T3<A, B, C>, T> tupled() {
        return t -> apply(t._1, t._2, t._3);
    }

}
