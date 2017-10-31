package ch.awae.utils.statemachine;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Builder for constructing state machines.
 * <p>
 * A state machine consists of one or more state machine cores. There cores are
 * constructed using a {@link MachineCoreBuilder}.
 * </p>
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 0.0.3
 * @version 1.2 (0.0.4)
 * 
 * @see StateMachine
 * @see MachineCoreBuilder
 */
public class StateMachineBuilder {

    private ArrayList<MachineCoreBuilder> cores = new ArrayList<>();

    /**
     * creates a new empty builder
     */
    public StateMachineBuilder() {
        super();
    }

    /**
     * copy constructor
     * 
     * @param builder
     *            the builder to copy
     * @throws NullPointerException
     *             {@code builder} is {@code null}
     * @since 1.2
     */
    public StateMachineBuilder(StateMachineBuilder builder) {
        Objects.requireNonNull(builder);
        synchronized (builder.cores) {
            cores.addAll(builder.cores);
        }
    }

    /**
     * Adds a {@link MachineCoreBuilder} to this builder. That builder will be
     * used to derive a state machine core internally. The passed
     * {@link MachineCoreBuilder} instance will be copied and will therefore not
     * be affected by further changes to that instance.
     * 
     * @param builder
     *            the core builder to add. may not be {@code null}
     * @return the builder itself
     * @throws NullPointerException
     *             {@code builder} is {@code null}
     */
    public StateMachineBuilder addMachineCore(MachineCoreBuilder builder) {
        Objects.requireNonNull(builder);
        MachineCoreBuilder bldr = builder.copy();
        synchronized (cores) {
            cores.add(bldr);
        }
        return this;
    }

    /**
     * creates a copy of this builder
     * 
     * @return the copy
     * @since 1.2
     */
    public StateMachineBuilder copy() {
        return new StateMachineBuilder(this);
    }

    /**
     * Constructs a {@link StateMachine} represented by this builder and all its
     * {@link MachineCoreBuilder MachineCoreBuilders}
     * 
     * @return a state machine
     * @throws IllegalArgumentException
     *             a core could not be constructed due to invalid data
     */
    public StateMachine build() {
        MachineCore[] cores = new MachineCore[this.cores.size()];
        for (int i = 0; i < cores.length; i++) {
            try {
                cores[i] = this.cores.get(i).build();
            } catch (
                    NullPointerException
                    | IllegalArgumentException ex) {
                throw new IllegalArgumentException("core[" + i + "] could not be constructed:\n" + ex.getMessage(), ex);
            }
        }
        return new StateMachineImpl(cores);
    }

}
