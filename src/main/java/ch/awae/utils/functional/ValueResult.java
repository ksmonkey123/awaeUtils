package ch.awae.utils.functional;

import java.util.NoSuchElementException;
import java.util.Objects;

class ValueResult<T> extends Result<T> {

    private final T value;

    ValueResult(T value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    @Override
    public T get() {
        return this.value;
    }

    @Override
    public Throwable exception() {
        throw new NoSuchElementException();
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public boolean isErroneous() {
        return false;
    }

    @Override
    public <E> Result<E> map(FailableFunction1<T, E> mapper) {
        try {
            E result = mapper.apply(this.value);
            if (result != null)
                return new ValueResult<>(result);
            return new EmptyResult<>();
        } catch (Throwable e) {
            return new ErrorResult<>(e);
        }
    }

    @Override
    public <E> Result<E> flatMap(FailableFunction1<T, Result<E>> mapper) {
        try {
            return mapper.apply(this.value);
        } catch (Throwable ex) {
            return new ErrorResult<>(ex);
        }
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
    public boolean isEmpty() {
        return false;
    }

    @Override
    public String toString() {
        return "VALUE( " + this.value.toString() + " )";
    }

}
