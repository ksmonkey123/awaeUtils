package ch.awae.utils;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Wrapper Object for lazy initialisation.
 * 
 * Lazy Initialisation allows delaying the initialisation until the value is
 * first required. This implementation provides a generic thread-safe wrapper
 * therefore.
 * 
 * @author Andreas Wälchli
 * @version 1.2, 2015-05-09
 *
 * @param <T>
 *            The Content type
 */
public final class Lazy<T> {

    private boolean needsInit = false;
    private T value;
    private final Supplier<? extends T> supplier;
    private final Object LOCKER = new Object();

    /**
     * Creates a new lazy instance based on the provided supplier method
     * 
     * @param supplier
     *            the supplier to evaluate when first requiring a value
     */
    public Lazy(Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier);
        this.supplier = supplier;
    }

    /**
     * Retrieves the value and calculates it when required
     * 
     * @return the value
     */
    public T get() {
        if (this.needsInit) {
            synchronized (this.LOCKER) {
                if (this.needsInit) {
                    this.value = this.supplier.get();
                    this.needsInit = false;
                }
            }
        }
        return this.value;
    }

    /**
     * Indicates whether or not the value has already been initialised.
     * 
     * @return {@code true} if the value is already initialised, {@code false}
     *         otherwise.
     */
    public boolean isInitialised() {
        return !this.needsInit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (!(o instanceof Lazy))
            return false;
        Lazy<?> other = (Lazy<?>) o;
        if (this.value == null && this.value == other.value)
            return true;
        return this.value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return this.get().hashCode();
    }
}
