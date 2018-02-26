package ch.awae.utils.functional;

@FunctionalInterface
public interface FailableRunnable {

    void run() throws Throwable;

}
