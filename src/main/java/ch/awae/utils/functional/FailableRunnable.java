package ch.awae.utils.functional;

/**
 * Similar to {@link java.lang.Runnable} but able to throw any arbitrary
 * exception.
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.0
 */
@FunctionalInterface
public interface FailableRunnable {

    void run() throws Throwable;

}
