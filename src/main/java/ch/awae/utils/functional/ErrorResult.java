package ch.awae.utils.functional;

import java.util.NoSuchElementException;

class ErrorResult<T> extends Result<T> {

    private final Throwable e;

    ErrorResult(Throwable e) {
        this.e = e;
    }

    @Override
    public T get() {
        throw new NoSuchElementException("erroneous result: " + this.e);
    }

    @Override
    public Throwable exception() {
        return this.e;
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public boolean isErroneous() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public <E> Result<E> map(FailableFunction1<T, E> mapper) {
        return new ErrorResult<>(this.e);
    }

    @Override
    public <E> Result<E> flatMap(FailableFunction1<T, Result<E>> mapper) {
        return new ErrorResult<>(this.e);
    }

    @Override
    public Result<T> mapException(FailableFunction1<Throwable, T> mapper) {
        try {
            T result = mapper.apply(this.e);
            if (result != null)
                return new ValueResult<>(result);
            return new EmptyResult<>();
        } catch (Throwable ex) {
            return new ErrorResult<>(ex);
        }
    }

    @Override
    public Result<T> flatMapException(FailableFunction1<Throwable, Result<T>> mapper) {
        try {
            return mapper.apply(this.e);
        } catch (Throwable ex) {
            return new ErrorResult<>(ex);
        }
    }

    @Override
    public String toString() {
        return "ERROR( " + this.e.toString() + " @ " + this.e.getStackTrace()[0] + " )";
    }
}
