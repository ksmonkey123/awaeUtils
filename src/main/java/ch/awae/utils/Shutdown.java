package ch.awae.utils;

import java.util.Objects;

import ch.awae.utils.collection.mutable.PriorityQueue;

/**
 * This class serves as a wrapper for the Java-side Shutdown handlers with the
 * added option to define the shutdown order.
 *
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.0
 */
public final class Shutdown {

    private final PriorityQueue<Runnable> queue;

    /**
     * Creates a new Shutdown instance. On instantiation the shutdown instance
     * is automatically registered to the global shutdown handling.
     */
    public Shutdown() {
        this.queue = PriorityQueue.maxQueue();
        Runtime.getRuntime().addShutdownHook(new Thread(this::runShutdown));
    }

    /**
     * The default priority.
     *
     * @see #add(Runnable)
     * @see #add(Runnable, int)
     */
    public static final int DEFAULT = 0;

    private static final Shutdown defaultShutdown = new Shutdown();

    /**
     * The default priority for operations that should be performed very early
     * in the sequence.
     *
     * @see #add(Runnable, int)
     */
    public static final int EARLIER = 10000;

    /**
     * The default priority for operations that should be performed rather early
     * in the sequence.
     *
     * @see #add(Runnable, int)
     */
    public static final int EARLY = 1000;

    /**
     * The highest possible priority. This should only be used if an operation
     * has to be performed strictly first.
     *
     * @see #add(Runnable, int)
     */
    public static final int FIRST = Integer.MAX_VALUE;

    /**
     * The lowest possible priority. This should only be used if an operation
     * has to be performed strictly last.
     *
     * @see #add(Runnable, int)
     */
    public static final int LAST = Integer.MIN_VALUE;

    /**
     * The default priority for operations that should be performed rather late
     * in the sequence.
     *
     * @see #add(Runnable, int)
     */
    public static final int LATE = -1000;

    /**
     * The default priority for operations that should be performed very late in
     * the sequence.
     *
     * @see #add(Runnable, int)
     */
    public static final int LATER = -10000;

    /**
     * Provides a global Shutdown handler.
     *
     * @return the global Shutdown handler
     */
    public static Shutdown getDefaultShutdown() {
        return Shutdown.defaultShutdown;
    }

    /**
     * adds a {@link Runnable} with default priority ({@code 0}) to the shutdown
     * system.<br>
     * {@inheritDoc}
     *
     * @throws NullPointerException
     *             if the {@code r} argument is {@code null}
     */
    public boolean add(Runnable r) {
        Objects.requireNonNull(r, "no null runnable allowed");
        queue.add(r, DEFAULT);
        return true;
    }

    /**
     * @throws NullPointerException
     *             if the {@code r} argument is {@code null}
     *  1.3
     */
    public void add(Runnable r, double priority) {
        Objects.requireNonNull(r, "no null runnable allowed");
        queue.add(r, priority);
    }

    private final void runShutdown() {
        
        while (!queue.isEmpty()) {
            queue.poll().run();
        }
    }

}
