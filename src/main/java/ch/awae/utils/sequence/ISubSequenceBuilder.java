package ch.awae.utils.sequence;

/**
 * sequence builder for sub-sequences. Subsequences are created by starting
 * loops
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 0.0.6
 */
public interface ISubSequenceBuilder<T extends ISequenceBuilder<T>> extends ISequenceBuilder<ISubSequenceBuilder<T>> {

    /**
     * end the loop represented by this sub-sequence
     * 
     * @return the parent sequence builder
     */
    T end();

}
