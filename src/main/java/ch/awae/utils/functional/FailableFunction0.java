package ch.awae.utils.functional;

@FunctionalInterface
public interface FailableFunction0<T> {

    T apply() throws Throwable;
    
}
