package ch.awae.utils.sequence;

import ch.awae.utils.functional.InterruptibleRunnable;

final class SubSequenceBuilder<T extends ISequenceBuilder<T>> implements ISubSequenceBuilder<T> {

    private final int iterations;
    private IRootSequenceBuilder base = Sequence.builder();
    private final T parent;
    private final boolean infinite;

    SubSequenceBuilder(T parent, int iterations, boolean infinite) {
        this.parent = parent;
        this.iterations = iterations;
        this.infinite = infinite;
    }

    @Override
    public ISubSequenceBuilder<T> step(InterruptibleRunnable step) {
        base = base.step(step);
        return this;
    }

    @Override
    public ISubSequenceBuilder<ISubSequenceBuilder<T>> loop(int iterations) {
        return new SubSequenceBuilder<ISubSequenceBuilder<T>>(this, iterations, false);
    }

    @Override
    public T end() {
        InterruptibleRunnable raw = base.compileRaw();
        if (infinite) {
            return parent.step(() -> {
                while (true) {
                    raw.run();
                }
            });
        } else {
            return parent.step(() -> {
                for (int i = 0; i < iterations; i++) {
                    raw.run();
                }
            });
        }
    }

}
