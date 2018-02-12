package ch.awae.utils.sequence;

import ch.awae.utils.functional.InterruptableRunnable;

public interface ISequenceBuilder<T extends ISequenceBuilder<T>> {

    T step(InterruptableRunnable step);

    default T sleep(long millis) {
        return step(() -> Thread.sleep(millis));
    }

    ISubSequenceBuilder<T> loop(int iterations);

}
