package ch.awae.utils;

import java.util.function.Supplier;

/**
 * Access Flags encapsulate the access flag pattern where a flag is used to
 * enable or disable access to certain functions.
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.2
 */
public final class AccessFlag {

    private volatile boolean locked;
    private final Supplier<RuntimeException> exceptionSupplier;

    public AccessFlag() {
        this(false);
    }

    public AccessFlag(String name) {
        this(name, false);
    }

    public AccessFlag(boolean locked) {
        this(locked, () -> new IllegalStateException("access is locked"));
    }

    public AccessFlag(String name, boolean locked) {
        this(locked, () -> new IllegalStateException("access to " + name + " is locked"));
    }

    public AccessFlag(boolean locked, Supplier<RuntimeException> exceptionSupplier) {
        this.locked = locked;
        this.exceptionSupplier = exceptionSupplier;
    }

    public synchronized void lock() {
        locked = true;
    }

    public synchronized void unlock() {
        locked = false;
    }

    public synchronized void toggleLock() {
        locked = !locked;
    }

    public boolean isLocked() {
        return locked;
    }

    /**
     * Tests if the access is locked.
     * 
     * If it is locked an exception is thrown. By default this is an
     * {@link IllegalStateException}.
     */
    public synchronized void test() {
        if (locked)
            throw exceptionSupplier.get();
    }

    /**
     * Tests if the access is locked. Locks if unlocked.
     * 
     * If it is locked an exception is thrown. By default this is an
     * {@link IllegalStateException}. If it is unlocked, it will be locked.
     */
    public synchronized void testAndLock() {
        test();
        lock();
    }

    /**
     * Tests if the access is locked. Locks if unlocked, executes the provided
     * {@link Runnable} and then unlocks.
     * 
     * If it is locked an exception is thrown. By default this is an
     * {@link IllegalStateException}.
     * 
     * If the {@code runnable} throws an exception, the lock will <em>not</em>
     * be lifted!
     * 
     * @param runnable
     *            the runnable to execute during the locked period
     */
    public synchronized void lockWhile(Runnable runnable) {
        testAndLock();
        runnable.run();
        unlock();
    }

}
