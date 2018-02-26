package ch.awae.utils.functional;

import java.util.function.Function;

final class Success<T> implements Try<T> {

    private final T value;

    Success(T value) {
        this.value = value;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public T get() throws Throwable {
        return value;
    }

    @Override
    public Throwable getFailure() {
        throw new NullPointerException();
    }

    @Override
    public <S> Try<S> map(FailableFunction1<T, S> f) {
        try {
            return Try.success(f.apply(value));
        } catch (Throwable e) {
            return Try.failure(e);
        }
    }

    @Override
    public <S> Try<S> flatMap(Function<T, Try<S>> f) {
        return f.apply(value);
    }

    @Override
    public Try<T> recover(FailableFunction1<Throwable, Try<T>> f) {
        return this;
    }

}
