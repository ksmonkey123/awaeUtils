package ch.awae.utils.functional;

import java.util.concurrent.Callable;

/**
 * Similar to {@link java.util.function.Supplier} but able to throw any
 * arbitrary exception.
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.0
 */
@FunctionalInterface
public interface FailableFunction0<T> extends Callable<T> {

    T apply() throws Throwable;

    default <S> FailableFunction0<S> andThen(FailableFunction1<T, S> f) {
        return () -> f.apply(apply());
    }

    @Override
    default T call() throws Exception {
        try {
            return apply();
        } catch (Throwable e) {
            if (e instanceof Exception)
                throw (Exception) e;
            else if (e instanceof Error)
                throw (Error) e;
            else
                throw new Exception(e);
        }
    }

}
