package ch.awae.utils.sequence;

import ch.awae.utils.functional.InterruptableRunnable;

public interface IRootSequenceBuilder extends ISequenceBuilder<IRootSequenceBuilder> {

    ISubSequenceBuilder<IRootSequenceBuilder> loop();

    InterruptableRunnable compileRaw();

    default Sequence compile() {
        return new Sequence(compileRaw());
    }

}
