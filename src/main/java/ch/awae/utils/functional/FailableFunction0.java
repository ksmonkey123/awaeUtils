package ch.awae.utils.functional;

/**
 * Similar to {@link java.util.function.Supplier} but able to throw any
 * arbitrary exception.
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.0
 */
@FunctionalInterface
public interface FailableFunction0<T> {

    T apply() throws Throwable;

}
