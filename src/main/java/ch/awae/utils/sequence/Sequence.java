package ch.awae.utils.sequence;

import ch.awae.utils.functional.InterruptableRunnable;

public final class Sequence {

    private final Runnable base;
    private Thread thread = null;

    public Sequence(InterruptableRunnable base) {
        this.base = () -> {
            try {
                base.run();
            } catch (InterruptedException e) {
                // no handling required
            }
        };
    }

    public synchronized void start() throws InterruptedException {
        // already running thread should be stopped first
        stop();
        thread = new Thread(base);
        thread.start();
    }

    public synchronized void join() throws InterruptedException {
        if (thread != null)
            thread.join();
    }

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

    public static IRootSequenceBuilder builder() {
        return new RootSequenceBuilder();
    }

}
