package ch.awae.utils.functional;

/**
 * Similar to {@link java.lang.Runnable} but with the ability to be interrupted.
 * This can be used instead of {@link FailableRunnable} if only
 * {@link InterruptedException} should be allowed.
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 0.0.6
 */
public interface InterruptibleRunnable {

    void run() throws InterruptedException;

}
