package ch.awae.utils.sequence;

import ch.awae.utils.functional.InterruptableRunnable;

final class RootSequenceBuilder implements IRootSequenceBuilder {

    private InterruptableRunnable[] elements;

    RootSequenceBuilder(InterruptableRunnable... els) {
        elements = els;
    }

    public RootSequenceBuilder step(InterruptableRunnable r) {
        InterruptableRunnable[] next = new InterruptableRunnable[elements.length + 1];
        System.arraycopy(elements, 0, next, 0, elements.length);
        next[elements.length] = r;
        return new RootSequenceBuilder(next);
    }

    public InterruptableRunnable compileRaw() {
        return () -> {
            for (InterruptableRunnable r : elements) {
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
