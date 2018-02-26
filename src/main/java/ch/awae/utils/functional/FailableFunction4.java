package ch.awae.utils.functional;

/**
 * Similar to {@link java.util.function.Function} but able to throw any
 * arbitrary exception.
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.0
 */
@FunctionalInterface
public interface FailableFunction4<A, B, C, D, T> {

    T apply(A a, B b, C c, D d) throws Throwable;

    default <S> FailableFunction4<A, B, C, D, S> andThen(FailableFunction1<T, S> f) {
        return (a, b, c, d) -> f.apply(this.apply(a, b, c, d));
    }

    default FailableFunction1<T4<A, B, C, D>, T> tupled() {
        return t -> apply(t._1, t._2, t._3, t._4);
    }

}
