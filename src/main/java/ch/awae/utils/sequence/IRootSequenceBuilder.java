package ch.awae.utils.sequence;

import ch.awae.utils.functional.InterruptibleRunnable;

/**
 * Sequence Builder for the root sequence.
 * 
 * The root sequence is the main sequence. Sub-sequences can be created by
 * adding loops.
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 0.0.6
 */
public interface IRootSequenceBuilder extends ISequenceBuilder<IRootSequenceBuilder> {

    /**
     * Start an infinite loop
     * 
     * @return subsequence builder
     */
    ISubSequenceBuilder<IRootSequenceBuilder> loop();

    /**
     * compile the sequence into a raw interruptible runnable
     */
    InterruptibleRunnable compileRaw();

    /**
     * compile the constructed sequence into a full {@link Sequence} instance
     */
    default Sequence compile() {
        return new Sequence(compileRaw());
    }

}
