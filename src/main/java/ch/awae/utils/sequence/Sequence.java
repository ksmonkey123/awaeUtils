package ch.awae.utils.sequence;

import ch.awae.utils.functional.InterruptibleRunnable;

/**
 * Sequence allows for sequential construction of a sequence of code blocks.
 * Sequences may contain (nested) loops.
 * 
 * Sequences usually run in their own thread that can be started and stopped.
 * Stopping the Sequence tries to interrupt the execution as soon as possible.
 * If a step accepts the interrupt by throwing an {@link InterruptedException}
 * the sequence is aborted. In any case the sequence is aborted as soon as the
 * step running while calling {@link #stop()} terminates.
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 0.0.6
 */
public final class Sequence {

    private final Runnable base;
    private Thread thread = null;

    /**
     * Wrap an {@link InterruptibleRunnable} into a sequence instance
     */
    public Sequence(InterruptibleRunnable base) {
        this.base = () -> {
            try {
                base.run();
            } catch (InterruptedException e) {
                // no handling required
            }
        };
    }

    /**
     * Starts the sequence. If the sequence is already running it is terminated
     * and restarted. The termination is done by calling {@link #stop()} and is
     * therefore blocking.
     * 
     * @throws InterruptedException
     *             if the calling thread is interrupted while waiting for the
     *             previous instance of this sequence to terminate.
     * @see #stop()
     */
    public synchronized void start() throws InterruptedException {
        // already running thread should be stopped first
        stop();
        thread = new Thread(base);
        thread.start();
    }

    /**
     * Waits for the currently running instance of this sequence to terminate.
     * If the sequence is not currently running this method does nothing. This
     * does not stop the sequence.
     * 
     * @throws InterruptedException
     *             if the calling thread is interrupted while waiting for the
     *             running instance of this sequence to terminate.
     */
    public synchronized void join() throws InterruptedException {
        if (thread != null)
            thread.join();
    }

    /**
     * Stops the sequence. If the sequence is not running this method does
     * nothing. The termination of a sequence is blocking.
     * 
     * @throws InterruptedException
     *             if the calling thread is interrupted while waiting for the
     *             running instance of this sequence to terminate.
     */
    public synchronized void stop() throws InterruptedException {
        try {
            if (thread != null) {
                thread.interrupt();
                thread.join();
            }
        } finally {
            thread = null;
        }
    }

    /**
     * Creates a new sequence builder
     */
    public static IRootSequenceBuilder builder() {
        return new RootSequenceBuilder();
    }

}
