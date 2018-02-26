package ch.awae.utils.functional;

@FunctionalInterface
public interface FailableFunction1<A, B> {

    B apply(A a) throws Throwable;

    default <C> FailableFunction1<A, C> andThen(FailableFunction1<B, C> f) {
        return a -> f.apply(this.apply(a));
    }

}