package ch.awae.utils.sequence;

public interface ISubSequenceBuilder<T extends ISequenceBuilder<T>> extends ISequenceBuilder<ISubSequenceBuilder<T>> {

    T end();
    
}
