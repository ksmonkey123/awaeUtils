package ch.awae.utils.functional;

import java.util.NoSuchElementException;

class EmptyResult<T> extends Result<T> {

    @Override
    public T get() {
        throw new NoSuchElementException();
    }

    @Override
    public Throwable exception() {
        throw new NoSuchElementException();
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public boolean isErroneous() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public <E> Result<E> map(FailableFunction1<T, E> mapper) {
        return new EmptyResult<>();
    }

    @Override
    public <E> Result<E> flatMap(FailableFunction1<T, Result<E>> mapper) {
        return new EmptyResult<>();
    }

    @Override
    public Result<T> mapException(FailableFunction1<Throwable, T> mapper) {
        return this;
    }

    @Override
    public Result<T> flatMapException(FailableFunction1<Throwable, Result<T>> mapper) {
        return this;
    }

    @Override
    public String toString() {
        return "NONE()";
    }
}
