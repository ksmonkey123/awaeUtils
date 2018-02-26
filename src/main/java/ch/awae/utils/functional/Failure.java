package ch.awae.utils.functional;

import java.util.function.Function;

@SuppressWarnings("rawtypes")
final class Failure implements Try {

    private Throwable throwable;

    public Failure(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public Object get() throws Throwable {
        throw throwable;
    }

    @Override
    public Throwable getFailure() {
        return throwable;
    }

    @Override
    public Try map(FailableFunction1 f) {
        return this;
    }

    @Override
    public Try flatMap(Function f) {
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Try recover(FailableFunction1 f) {
        try {
            return (Try) f.apply(throwable);
        } catch (Throwable subsequent) {
            return Try.failure(subsequent);
        }
    }

}
