package ch.awae.utils.statemachine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Builder for constructing state machine cores. Multiple of these builders can
 * be combined in a {@link StateMachineBuilder} to construct a state machine.
 * 
 * <p>
 * A machine core describes a single state machine with exactly one active state
 * at any time. Multiple cores can be combined into a {@link StateMachine} at
 * which point they are combined into a state machine cluster with common event
 * and command queues. The builder does perform some preliminary data validation
 * on every mutation. A full data validation is performed by the
 * {@link StateMachineBuilder} whenever a core is added.
 * </p>
 * <h3>Machine Core construction</h3>
 * <p>
 * A machine core is constructed by defining a list of transitions. From these
 * transitions the states are extracted automatically. Transitions can be added
 * by calling
 * {@link #addTransition(String, String, String, String[], String[])}. Every
 * machine core requires an initial state to be defined. That is the state the
 * machine core starts in and is reverted to whenever a state machine is reset
 * using {@link StateMachine#reset()}. This initial state can be set through
 * {@link #setInitialState(String)}.
 * </p>
 * <h4>Helper Methods</h4>
 * <p>
 * The method {@link #addSequence(String, String[], String, String[], String[])}
 * allows for easy definition of a <em>transition sequence</em>, i.e. an ordered
 * list of events that - if occurring in that sequence - result in a given
 * transition. The intermediate states are generated internally and are expected
 * to be unique (the state names are derived from random {@link UUID}'s).
 * </p>
 * <p>
 * The method
 * {@link #addArbitrarySequence(String, String[], String, String[], String[])}
 * allows for easy definition of a set of transitions for a given unordered list
 * of events. The resulting transitions represent a system where all events in
 * the given list must occur in any order to result in a given transition. The
 * resulting system grows very quickly with the number of events in the list. It
 * is recommended to avoid exceeding 4-5 entries as otherwise many thousands of
 * states may be created.
 * </p>
 * <p>
 * The method {@link #addFunnel(String[], String, String, String[], String[])}
 * allows for easy definition of the same transition from multiple origins to a
 * single target state. This is very useful if there are multiple states that
 * should respond to a given event identically. If combined with the
 * {@link #addSequence(String, String[], String, String[], String[])} method
 * this can for example be used to create "reset" transitions for each
 * intermediate state in the sequence.
 * </p>
 * <h3>Terminal State Checking</h3>
 * <p>
 * By default during creation each core validates that there are no terminal
 * states (i.e. states that have no transitions leaving them). Often terminal
 * states are not desired as most state machines should be able (by some
 * sequence of events) to return to a prior well-defined state (e.g. a default
 * "idle" state). In these scenarios terminal states are an indicator of a
 * programming error and are therefore checked for. This checks can however be
 * very expensive as the target states of all transitions must be checked. If
 * terminal states are actually needed, the check strictness can be reduced by
 * calling {@link #setAllowTerminalStates(boolean)
 * setAllowTerminalStates(true)}. This will still perform the check but only
 * produce warnings for terminal states and not an exception. If the performance
 * requirements require it the terminal state check can be disabled completely
 * by calling {@link #setCheckForTerminalStates(boolean)
 * setCheckForTerminalStates(false)}. It is recommended to keep the terminal
 * state check enabled whenever possible but at least for testing. If all
 * "legal" terminal states are known the check still allows for easy detection
 * of unwanted terminal states.
 * </p>
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 0.0.3
 * @version 1.4 (0.0.5)
 * 
 * @see StateMachine
 * @see StateMachineBuilder
 */
public final class MachineCoreBuilder {

    private ArrayList<Transition> transitions   = new ArrayList<>();
    private String                initialState  = null;
    private boolean               allowTerminal = false;
    private boolean               checkTerminal = true;

    private final Object LOCK = new Object();

    /**
     * creates a new empty builder
     */
    public MachineCoreBuilder() {
        super();
    }

    /**
     * copy constructor
     * 
     * @param builder
     *            the builder to copy
     * @throws NullPointerException
     *             {@code builder} is {@code null}
     * @since 1.2
     */
    public MachineCoreBuilder(MachineCoreBuilder builder) {
        Objects.requireNonNull(builder);
        // copy data
        checkTerminal = builder.checkTerminal;
        initialState = builder.initialState;
        allowTerminal = builder.allowTerminal;
        synchronized (builder.LOCK) {
            transitions.addAll(builder.transitions);
        }
    }

    /**
     * Defines if the core should be checked for terminal states during
     * creation. This check may be very expensive. By default it is enabled.
     * 
     * @param check
     *            {@code true} if the check should be done.
     * @returns the builder itself
     * @since 1.4 (0.0.5)
     */
    public MachineCoreBuilder setCheckForTerminalStates(boolean check) {
        checkTerminal = check;
        return this;
    }

    /**
     * Adds a new transition between two states.
     * 
     * @param from
     *            the state the transition originates from. may not be
     *            {@code null}
     * @param event
     *            the event that triggers the transition. may not be
     *            {@code null}
     * @param to
     *            the state the transition leads to. may be identical to
     *            {@code from}. may not be {@code null}
     * @param events
     *            an array of all events that shall be triggered by the
     *            transition. may be {@code null}. no element may be
     *            {@code null}
     * @param commands
     *            an array of all commands that shall be triggered by the
     *            transition. may be {@code null}. no element may be
     *            {@code null}
     * @return the builder itself
     * @throws NullPointerException
     *             if any {@code String} parameter or any array element is
     *             {@code null}
     */
    public MachineCoreBuilder addTransition(String from, String event, String to, String[] events, String[] commands) {
        // recursive resolution of null arrays
        if (events == null)
            return addTransition(from, event, to, new String[0], commands);
        if (commands == null)
            return addTransition(from, event, to, events, new String[0]);

        // preliminary input validation
        Objects.requireNonNull(from, "'from' may not be null");
        Objects.requireNonNull(event, "'event' may not be null");
        Objects.requireNonNull(commands, "'commands' may not be null");

        // construct command array
        Command[] cmds = new Command[events.length + commands.length];

        for (int i = 0; i < events.length; i++)
            cmds[i] = new Command(CommandType.EVENT,
                    Objects.requireNonNull(events[i], "'events[" + i + "]' may not be null"));

        for (int i = 0; i < commands.length; i++)
            cmds[i + events.length] = new Command(CommandType.COMMAND,
                    Objects.requireNonNull(commands[i], "'commands[" + i + "]' may not be null"));

        // add transition
        Transition transition = new Transition(from, event, to, cmds);
        synchronized (LOCK) {
            transitions.add(transition);
        }
        return this;
    }

    /**
     * Adds a new transition from all the origin states to a single target state
     * essentially funnelling the state flow.
     * 
     * @param from
     *            the states the transitions originate from. may not be
     *            {@code null} or contain {@code null} elements
     * @param event
     *            the event that triggers the transition. may not be
     *            {@code null}
     * @param to
     *            the state the transition leads to. may be identical to
     *            {@code from}. may not be {@code null}
     * @param events
     *            an array of all events that shall be triggered by the
     *            transition. may be {@code null}. no element may be
     *            {@code null}
     * @param commands
     *            an array of all commands that shall be triggered by the
     *            transition. may be {@code null}. no element may be
     *            {@code null}
     * @return the builder itself
     * @throws NullPointerException
     *             if any {@code String} parameter or any array element is
     *             {@code null}
     * @since 1.4 (0.0.5)
     */
    public MachineCoreBuilder addFunnel(String[] from, String event, String to, String[] events, String[] commands) {
        // recursive resolution of null arrays
        if (events == null)
            return addFunnel(from, event, to, new String[0], commands);
        if (commands == null)
            return addFunnel(from, event, to, events, new String[0]);

        // preliminary input validation
        Objects.requireNonNull(from, "'from' may not be null");
        Objects.requireNonNull(event, "'event' may not be null");
        Objects.requireNonNull(commands, "'commands' may not be null");
        if (from.length == 0)
            throw new IllegalArgumentException("empty 'from' array is not allowed");
        for (int i = 0; i < from.length; i++)
            Objects.requireNonNull(from[i], "'from[" + i + "]' may not be null");

        // construct command array
        Command[] cmds = new Command[events.length + commands.length];

        for (int i = 0; i < events.length; i++)
            cmds[i] = new Command(CommandType.EVENT,
                    Objects.requireNonNull(events[i], "'events[" + i + "]' may not be null"));

        for (int i = 0; i < commands.length; i++)
            cmds[i + events.length] = new Command(CommandType.COMMAND,
                    Objects.requireNonNull(commands[i], "'commands[" + i + "]' may not be null"));

        // add transition
        for (String state : from) {
            Transition transition = new Transition(state, event, to, cmds);
            synchronized (LOCK) {
                transitions.add(transition);
            }
        }
        return this;
    }

    /**
     * define if terminal states are allowed. terminal states without any
     * transitions leaving them. By default terminal states are not allowed.
     * 
     * @param allow
     *            {@code true} if terminal states should be allowed
     * @return the builder itself
     * 
     * @since 1.4
     */
    public MachineCoreBuilder setAllowTerminalStates(boolean allow) {
        this.allowTerminal = allow;
        return this;
    }

    /**
     * Adds a sequence of state transitions with anonymous states for the
     * intermediate steps. The triggered events and commands will all be added
     * to the last transition in the sequence.
     * 
     * @param from
     *            the state the transition originates from. may not be
     *            {@code null}
     * @param sequence
     *            the sequence of events that triggers the transitions. may not
     *            be {@code null} and must have at least one item.
     * @param to
     *            the state the transition leads to. may be identical to
     *            {@code from}. may not be {@code null}
     * @param events
     *            an array of all events that shall be triggered by the
     *            transition. may be {@code null}. no element may be
     *            {@code null}
     * @param commands
     *            an array of all commands that shall be triggered by the
     *            transition. may be {@code null}. no element may be
     *            {@code null}
     * @return an ordered list of all intermediate states or an empty array if
     *         no intermediate states exist
     * @throws NullPointerException
     *             if any {@code String} parameter or any array element is
     *             {@code null}
     * @throws IllegalArgumentException
     *             the sequence is empty
     * @since 1.3 (0.0.5)
     */
    public String[] addSequence(String from, String[] sequence, String to, String[] events, String[] commands) {
        // recursive resolution of null arrays
        if (events == null)
            return addSequence(from, sequence, to, new String[0], commands);
        if (commands == null)
            return addSequence(from, sequence, to, events, new String[0]);

        // preliminary input validation
        Objects.requireNonNull(from, "'from' may not be null");
        Objects.requireNonNull(sequence, "'seqence' may not be null");
        Objects.requireNonNull(commands, "'commands' may not be null");
        if (sequence.length == 0)
            throw new IllegalArgumentException("empty sequence is not allowed");
        for (int i = 0; i < sequence.length; i++)
            Objects.requireNonNull(sequence[i], "'sequence[" + i + "]' may not be null");
        // sequence of length 1 is just a transition
        if (sequence.length == 1) {
            addTransition(from, sequence[0], to, events, commands);
            return new String[0];
        }
        // build sequence
        String[] states = new String[sequence.length - 1];
        String state = from;
        for (int i = 0; i < sequence.length - 1; i++) {
            // intermediate state
            String uuid = UUID.randomUUID().toString();
            addTransition(state, sequence[i], uuid, null, null);
            state = uuid;
            states[i] = state;
        }
        // build last transition
        addTransition(state, sequence[sequence.length - 1], to, events, commands);
        return states;
    }

    /**
     * Sets the initial state of the state machine core. The initial state is
     * the state the state machine will start at and return to whenever it is
     * reset.
     * 
     * @param state
     *            the initial state. may not be {@code null}
     * @return the builder itself
     * @throws NullPointerException
     *             {@code state} is {@code null}
     */
    public MachineCoreBuilder setInitialState(String state) {
        initialState = Objects.requireNonNull(state, "'state' may not be null");
        return this;
    }

    /**
     * Creates a network of states and transitions covering all possible
     * permutations of a given list of events. Essentially this represents a
     * situation where every event must occur but the order is irrelevant. The
     * resulting network will grow very quickly. For a sequence of length
     * {@code n} there will be generated {@code n!} intermediate states.
     * Currently there is no optimisation performed on the network.
     * 
     * @param from
     *            the state the transition originates from. may not be
     *            {@code null}
     * @param sequence
     *            the list of events that triggers the transitions. may not be
     *            {@code null} and must have at least one item.
     * @param to
     *            the state the transition leads to. may be identical to
     *            {@code from}. may not be {@code null}
     * @param events
     *            an array of all events that shall be triggered by the
     *            transition. may be {@code null}. no element may be
     *            {@code null}
     * @param commands
     *            an array of all commands that shall be triggered by the
     *            transition. may be {@code null}. no element may be
     *            {@code null}
     * @return the builder itself
     * @throws NullPointerException
     *             if any {@code String} parameter or any array element is
     *             {@code null}
     * @throws IllegalArgumentException
     *             the sequence is empty
     * @since 1.4 (0.0.5)
     */
    public MachineCoreBuilder addArbitrarySequence(String from, String[] sequence, String to, String[] events,
            String[] commands) {
        // recursive resolution of null arrays
        if (events == null)
            return addArbitrarySequence(from, sequence, to, new String[0], commands);
        if (commands == null)
            return addArbitrarySequence(from, sequence, to, events, new String[0]);

        // preliminary input validation
        Objects.requireNonNull(from, "'from' may not be null");
        Objects.requireNonNull(sequence, "'seqence' may not be null");
        Objects.requireNonNull(commands, "'commands' may not be null");
        if (sequence.length == 0)
            throw new IllegalArgumentException("empty sequence is not allowed");
        for (int i = 0; i < sequence.length; i++)
            Objects.requireNonNull(sequence[i], "'sequence[" + i + "]' may not be null");
        // sequence of length 1 is just a transition
        if (sequence.length == 1)
            return addTransition(from, sequence[0], to, events, commands);
        // build sequence
        for (String event : sequence) {
            String state = UUID.randomUUID().toString();
            // build remainder
            ArrayList<String> remaining = new ArrayList<>();
            remaining.addAll(Arrays.asList(sequence));
            remaining.remove(event);
            // register transition
            addTransition(from, event, state, null, null);
            // register remaining network
            addArbitrarySequence(state, remaining.toArray(new String[0]), to, events, commands);
        }
        // done
        return this;
    }

    /**
     * Searches the target state for a given transition originating at a given
     * node
     * 
     * @param origin
     *            the state the transition originates at
     * @param event
     *            the event triggering the transition
     * @return the target state of the transition or {@code null} if no matching
     *         transition was found
     * 
     * @since 1.4 (0.0.5)
     */
    public String getTarget(String origin, String event) {
        synchronized (LOCK) {
            for (Transition transition : transitions) {
                if (!transition.origin.equals(origin))
                    continue;
                if (!transition.event.equals(event))
                    continue;
                return transition.target;
            }
        }
        return null;
    }

    /**
     * creates a copy of this builder instance.
     * 
     * @return a copy
     * @since 1.2
     * @see #MachineCoreBuilder(MachineCoreBuilder)
     */
    public MachineCoreBuilder copy() {
        return new MachineCoreBuilder(this);
    }

    MachineCore build(int id, String logname, Logger logger) {
        synchronized (LOCK) {
            return new MachineCore(id, logname, logger, !allowTerminal, checkTerminal, initialState,
                    transitions.toArray(new Transition[0]));
        }
    }

}
