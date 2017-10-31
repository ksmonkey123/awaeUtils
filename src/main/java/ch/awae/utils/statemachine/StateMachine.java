package ch.awae.utils.statemachine;

import java.util.concurrent.BlockingQueue;

/**
 * Base representation of a state machine or a cluster of state machines.
 * 
 * <p>
 * A state machine consists of a set of states and transition rules between
 * those states. Transitions are triggered by events. Events can be issued
 * through {@link #event(String)} A transition always has a target state (i.e.
 * the state the state machine should switch to during the transition) and a set
 * of side effects. These can be further events to be triggered or commands that
 * should be issued. Events (both external and internal) are collected in an
 * event queue and processed asynchronously by the state machine. Commands
 * triggered by transitions are collected in a command queue that is accessible
 * from outside the state machine. This allows user code to collect these
 * commands and to process them. A cluster of state machines is handled
 * similarly to a single state machine but with multiple active states at once
 * (i.e. one active state per state machine) and all events are applied to all
 * internal state machines. Commands issued by any of the internal state
 * machines are all collected in a common command queue.
 * </p>
 * <p>
 * State machines must be started to be able to process events. Use
 * {@link #start()} and {@link #stop()} to start or stop the state machine. A
 * stopped state machine can be restarted at any time. Restarting a state
 * machine simply resumes operation. A state machine can also be reset to the
 * initial state by calling {@link #reset()}. A reset will clear the event queue
 * and revert and reinitialise the state machine to its initial state. The
 * command queue remains unaffected as it is not considered as integral a part
 * to the state machine as the event queue is.
 * </p>
 * 
 * Note: state machines cannot be combined into a cluster as all machines in a
 * cluster share a common event queue. Clusters must be constructed directly.
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 0.0.3
 * @version 1.1
 */
public interface StateMachine {

    /**
     * Adds a new event to the internal event queue
     * 
     * @param event
     *            the event to issue. may not be {@code null}
     * @throws NullPointerException
     *             {@code event} is {@code null}
     */
    void event(String event);

    /**
     * Provides the command queue associated with the state machine. Use
     * {@link BlockingQueue#take()} on the queue to access the commands.
     * 
     * @return the command queue
     */
    BlockingQueue<String> getCommandQueue();

    /**
     * Starts processing of the state machine event queue.
     * 
     * @throws IllegalStateException
     *             the state machine is already running
     */
    void start();

    /**
     * Stops processing of the state machine event queue. This method blocks
     * until the processing thread has terminated.
     * 
     * @throws IllegalStateException
     *             the state machine is not running
     */
    void stop();

    /**
     * Resets the state machine to its initial state and clears the event queue.
     * Unprocessed events are discarded.
     * 
     * Note that this method will not block any calls to {@link #event(String)}
     * while the reset is in progress.
     */
    void reset();

    /**
     * Creates a graphviz (dot) compatible graph from the state machine.
     * 
     * @return a dot graph string
     */
    String extractDiagram();

}
