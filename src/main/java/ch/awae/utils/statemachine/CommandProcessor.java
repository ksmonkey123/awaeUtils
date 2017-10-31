package ch.awae.utils.statemachine;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

/**
 * A command processor that handles commands asynchronously directly from the
 * command queue of a {@link StateMachine}.
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 0.0.5
 * @version 1.1
 * 
 * @see StateMachine
 */
public final class CommandProcessor {

    private final BlockingQueue<String> queue;
    private final Consumer<String>      handler;

    private final Object LOCK   = new Object();
    private Thread       worker = null;

    /**
     * creates a new command processor from a {@link StateMachine}. It hooks
     * directly into the command queue of that {@link StateMachine} and handles
     * each command through a defined handling routine.
     * 
     * @param machine
     *            the state machine to process commands for. may not be
     *            {@code null}
     * @param handler
     *            a handler function defining the way each command is handled.
     *            may not be {@code null}
     * @throws NullPointerException
     *             a parameter is {@code null}
     * 
     * @see StateMachine
     */
    public CommandProcessor(StateMachine machine, Consumer<String> handler) {
        Objects.requireNonNull(machine, "'machine' may not be null");
        Objects.requireNonNull(handler, "'handler' may not be null");
        // initialise
        this.handler = handler;
        this.queue = machine.getCommandQueue();
        // check
        Objects.requireNonNull(queue, "the command queue may not be null");
    }

    /**
     * starts the processor
     * 
     * @throws IllegalStateException
     *             the processor is already running
     */
    public void start() {
        synchronized (LOCK) {
            if (worker != null)
                throw new IllegalStateException("already running");
            worker = new Thread(this::workerLoop);
            worker.start();
        }
    }

    /**
     * stops the processor. blocks until the processor has terminated
     * 
     * @throws IllegalStateException
     *             the processor is not running
     */
    public void stop() {
        synchronized (LOCK) {
            if (worker == null)
                throw new IllegalStateException("not running");
            worker.interrupt();
            // wait for the thread to die off
            boolean interrupted = false;
            while (true) {
                try {
                    worker.join();
                    break;
                } catch (InterruptedException e) {
                    // cache interrupt
                    interrupted = true;
                }
            }
            worker = null;
            // re-apply interrupt
            if (interrupted)
                Thread.currentThread().interrupt();
        }
    }

    private void workerLoop() {
        while (!Thread.interrupted()) {
            try {
                handler.accept(queue.take());
            } catch (InterruptedException e) {
                return;
            }
        }
    }

}
