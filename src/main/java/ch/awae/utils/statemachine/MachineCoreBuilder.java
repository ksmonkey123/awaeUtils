package ch.awae.utils.statemachine;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

/**
 * Builder for constructing state machine cores. multiple of these builders can
 * be combined in a {@link StateMachineBuilder} to construct a state machine.
 * 
 * <p>
 * A machine core describes a single state machine with exactly one active state
 * at any time. Multiple cores can be combined into a {@link StateMachine} at
 * which point they are combined into a state machine cluster with common event
 * and command queues.
 * </p>
 * <p>
 * The builder does perform some preliminary data validation on every mutation.
 * A full data validation is performed by the {@link StateMachineBuilder}
 * whenever a core is added.
 * </p>
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 0.0.3
 * @version 1.3 (0.0.4)
 * 
 * @see StateMachine
 * @see StateMachineBuilder
 */
public final class MachineCoreBuilder {

    private ArrayList<Transition> transitions  = new ArrayList<>();
    private String                initialState = null;

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
        initialState = builder.initialState;
        synchronized (builder.LOCK) {
            transitions.addAll(builder.transitions);
        }
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
     * @return the builder itself
     * @throws NullPointerException
     *             if any {@code String} parameter or any array element is
     *             {@code null}
     * @throws IllegalArgumentException
     *             the sequence is empty
     * @since 1.3 (0.0.5)
     */
    public MachineCoreBuilder addSequence(String from, String[] sequence, String to, String[] events,
            String[] commands) {
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
        if (sequence.length == 1)
            return addTransition(from, sequence[0], to, events, commands);
        // build sequence
        String state = from;
        for (int i = 0; i < sequence.length - 1; i++) {
            // intermediate state
            String uuid = UUID.randomUUID().toString();
            addTransition(state, sequence[i], uuid, null, null);
            state = uuid;
        }
        // build last transition
        addTransition(state, sequence[sequence.length - 1], to, events, commands);
        return this;
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
     * creates a copy of this builder instance.
     * 
     * @return a copy
     * @since 1.2
     * @see #MachineCoreBuilder(MachineCoreBuilder)
     */
    public MachineCoreBuilder copy() {
        return new MachineCoreBuilder(this);
    }

    MachineCore build() {
        synchronized (LOCK) {
            return new MachineCore(initialState, transitions.toArray(new Transition[0]));
        }
    }

}
