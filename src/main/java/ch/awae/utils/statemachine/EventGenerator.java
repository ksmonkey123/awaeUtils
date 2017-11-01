package ch.awae.utils.statemachine;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Asynchronous event generation.
 * <p>
 * Periodically repeat a fixed event or an event provided by a supplier
 * function.
 * </p>
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 0.0.5
 * @version 1.1
 * 
 * @see StateMachine
 */
public final class EventGenerator {

    private final StateMachine     machine;
    private final Supplier<String> supplier;
    private final long             delay;

    private final Object LOCK   = new Object();
    private Thread       worker = null;

    /**
     * creates a new event generator for a given event
     * 
     * @param machine
     *            the state machine to pass the events to
     * @param event
     *            the event to pass to the state machine
     * @param delay
     *            the delay in milliseconds between events. this is an
     *            approximate value and is subject to variations due to the
     *            system thread scheduler.
     * @throws NullPointerException
     *             the {@code machine} or {@code event} is {@code null}
     * @throws IllegalArgumentException
     *             the delay is zero or negative
     */
    public EventGenerator(StateMachine machine, String event, long delay) {
        // validate parameters
        Objects.requireNonNull(machine, "'machine' may not be null");
        Objects.requireNonNull(event, "'event' may not be null");
        if (delay <= 0)
            throw new IllegalArgumentException("'delay' must be positive");
        // initialise
        this.machine = machine;
        this.supplier = () -> event;
        this.delay = delay;
    }

    /**
     * creates a new event generator based off a {@code Supplier} function. It
     * is required that the {@code supplier} function does never return
     * {@code null} values as {@code null} events are not supported by the
     * {@code StateMachine}. If the {@code supplier} ever returns a {@code null}
     * value the worker thread of this generator will terminate with a
     * {@code NullPointerException}. The instance will however ensure that it
     * can be restarted.
     * 
     * @param machine
     *            the state machine to pass the events to
     * @param supplier
     *            the event supplier providing the events to pass to the state
     *            machine
     * @param delay
     *            the delay in milliseconds between events. this is an
     *            approximate value and is subject to variations due to the
     *            system thread scheduler.
     * @throws NullPointerException
     *             the {@code machine} or {@code supplier} is {@code null}
     * @throws IllegalArgumentException
     *             the delay is zero or negative
     */
    public EventGenerator(StateMachine machine, Supplier<String> supplier, long delay) {
        // validate parameters
        Objects.requireNonNull(machine, "'machine' may not be null");
        Objects.requireNonNull(supplier, "'supplier' may not be null");
        if (delay <= 0)
            throw new IllegalArgumentException("'delay' must be positive");
        // initialise
        this.machine = machine;
        this.supplier = supplier;
        this.delay = delay;
    }

    /**
     * starts the event generator
     * 
     * @throws IllegalStateException
     *             the generator is already running
     */
    public void start() {
        synchronized (LOCK) {
            if (worker != null)
                throw new IllegalStateException("already running");
            worker = new Thread(this::workLoop);
            worker.start();
        }
    }

    /**
     * stops the event generator. blocks until the generator terminates
     * 
     * @throws IllegalStateException
     *             the generator is not running
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
                    interrupted = true;
                }
            }
            worker = null;
            // re-issue interrupt
            if (interrupted)
                Thread.currentThread().interrupt();
        }
    }

    private void workLoop() {
        while (!Thread.interrupted()) {
            String event = supplier.get();
            // ensure event is valid
            if (event == null) {
                // disable yourself
                synchronized (LOCK) {
                    worker = null;
                }
                throw new NullPointerException("supplied event is null");
            }
            machine.event(event);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

}
