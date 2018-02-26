package ch.awae.utils.sequence;

import ch.awae.utils.functional.InterruptibleRunnable;

/**
 * Base interface for all sequence builders
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 0.0.6
 */
public interface ISequenceBuilder<T extends ISequenceBuilder<T>> {

    /**
     * adds a step to the sequence
     * 
     * @return the builder itself. for chaining
     */
    T step(InterruptibleRunnable step);

    /**
     * adds a step that sleeps for a given number of milliseconds. The step
     * makes a call to {@link Thread#sleep(long)} internally
     * 
     * @return the builder itself for chaining
     */
    default T sleep(long millis) {
        return step(() -> Thread.sleep(millis));
    }

    /**
     * start a finite loop with a given number of iterations
     * 
     * @param iterations
     *            the number of times the loop should be repeated
     * @return the bbuilder itself for chaining
     */
    ISubSequenceBuilder<T> loop(int iterations);

}
