package ch.awae.utils.sequence;

import ch.awae.utils.functional.InterruptibleRunnable;

final class RootSequenceBuilder implements IRootSequenceBuilder {

    private InterruptibleRunnable[] elements;

    RootSequenceBuilder(InterruptibleRunnable... els) {
        elements = els;
    }

    public RootSequenceBuilder step(InterruptibleRunnable r) {
        InterruptibleRunnable[] next = new InterruptibleRunnable[elements.length + 1];
        System.arraycopy(elements, 0, next, 0, elements.length);
        next[elements.length] = r;
        return new RootSequenceBuilder(next);
    }

    public InterruptibleRunnable compileRaw() {
        return () -> {
            for (InterruptibleRunnable r : elements) {
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                r.run();
            }
        };
    }

    @Override
    public ISubSequenceBuilder<IRootSequenceBuilder> loop(int iterations) {
        return new SubSequenceBuilder<IRootSequenceBuilder>(this, iterations, false);
    }

    @Override
    public ISubSequenceBuilder<IRootSequenceBuilder> loop() {
        return new SubSequenceBuilder<IRootSequenceBuilder>(this, 0, true);
    }

}
