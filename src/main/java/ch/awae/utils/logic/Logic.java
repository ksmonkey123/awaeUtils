package ch.awae.utils.logic;

import java.util.Objects;
import java.util.function.BooleanSupplier;

/**
 * Base interface for composeable boolean logic operating on a {@link Model}.
 * 
 * <p>
 * This interface can be implemented by any type capable of evaluating the
 * current {@link Model} state to a boolean value. This interface also
 * implements composition functions.
 * </p>
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 0.0.6
 */
@FunctionalInterface
public interface Logic {

    /**
     * prepares a function evaluating all given elements and returns a summary
     * representing all states as a number. Enables quick evaluation of a large
     * group of Logic instances and processing the result with a single switch
     * instead of a lot of if clauses.
     * 
     * Every instance evaluating to {@code true} is indicated as a 1. The first
     * parameter lies in the unit column, the second in the two's column etc.
     * 
     * {@code null} parameters will always yield 0 digits. This allows for
     * hex-alignment.
     * 
     * @param elements
     *            the Logic instances to evaluate. Limited to 32.
     * @return a function evaluating
     */
    static LogicCluster cluster(Logic... elements) {
        return new LogicCluster(elements);
    }

    /**
     * a logic instance that always evaluates to {@code true}
     */
    final static Logic TRUE = () -> true;

    /**
     * a logic instance that always evaluates to {@code false}
     */
    final static Logic FALSE = () -> false;

    /**
     * Evaluates the logic object to a boolean value. Usually checks if the
     * current Model state satisfies a defined condition.
     * 
     * @return a boolean evaluation of the logic expression
     */
    boolean evaluate();

    /**
     * Returns an instance with inverted logic. This inverted logic instance
     * evaluates this instance and inverts the result.
     * 
     * @return an instance with inverted logic
     */
    default Logic not() {
        return new NotLogic(this);
    }

    /**
     * Combines this logic instance with another one in an {@code AND}
     * operation.
     * 
     * <p>
     * The resulting logic instance evaluates to {@code true} iff both this and
     * the other instance evaluate to {@code true}. If either instance evaluates
     * to {@code false}, the result will be {@code false}.
     * </p>
     * 
     * @param other
     *            the logic instance to combine with this one
     * @return a logic instance evaluating to the {@code AND} composition of
     *         both this and the {@code other} logic instance
     * @throws NullPointerException
     *             if the {@code other} parameter is {@code null}
     */
    default Logic and(Logic other) {
        Objects.requireNonNull(other, "the 'other' instance may not be null!");
        return and(this, other);
    }

    /**
     * Combines this logic instance with another one in an {@code OR} operation.
     * 
     * <p>
     * The resulting logic instance evaluates to {@code true} if either this or
     * the other instance evaluates to {@code true} (or both).
     * </p>
     * 
     * @param other
     *            the logic instance to combine with this one
     * @return a logic instance evaluating to the {@code OR} composition of both
     *         this and the {@code other} logic instance
     * @throws NullPointerException
     *             if the {@code other} parameter is {@code null}
     */
    default Logic or(Logic other) {
        Objects.requireNonNull(other, "the 'other' instance may not be null!");
        return or(this, other);
    }

    /**
     * Creates an edge-triggering {@link Logic} instance.
     * 
     * Only evaluates to {@code true} the first time the base {@link Logic}
     * evaluates to {@code true} after previously evaluating to {@code false}.
     * This emulates edge triggering on a rising edge.
     * 
     * @return an edge triggering {@link Logic} instance
     */
    default Logic edge() {
        return new EdgeLogic(this);
    }

    // ====== FACTORY STYLE METHODS ======

    /**
     * Creates a new {@link Logic} instance from a {@code BooleanSupplier}
     * 
     * @param f
     *            the boolean supplier
     * @return a new {@link Logic} instance
     * @throws NullPointerException
     *             if the f parameter is {@code null}
     */
    static Logic from(BooleanSupplier f) {
        Objects.requireNonNull(f, "no parameter may be null!");
        return () -> f.getAsBoolean();
    }

    /**
     * Creates a {@link Logic} instance that combines all given logic instances
     * with an {@code OR} operation.
     * 
     * <p>
     * If any logic instance evaluates to {@code true} the combined logic
     * evaluates to {@code true}. The instance evaluates to {@code false} iff
     * all base logic instances also evaluate to {@code false}.
     * </p>
     * 
     * @param logic0
     *            the first logic instance
     * @param logics
     *            the remaining logic instances
     * @return a combined logic instance
     * @throws NullPointerException
     *             if any parameter is {@code null}, or the {@code logics} array
     *             contains {@code null} values.
     * @throws IllegalArgumentException
     *             if the {@code logics} array is empty
     */
    static Logic or(Logic logic0, Logic... logics) {
        Objects.requireNonNull(logic0, "no logic instance may be null!");
        Objects.requireNonNull(logics, "the logics array may not be null!");
        for (Logic l : logics)
            Objects.requireNonNull(l, "no logic instance may be null!");
        if (logics.length == 0)
            throw new IllegalArgumentException("logics array may not be empty!");
        // code
        return () -> {
            if (logic0.evaluate())
                return true;
            for (Logic logic : logics)
                if (logic.evaluate())
                    return true;
            return false;
        };
    }

    /**
     * Creates a {@link Logic} instance that combines all given logic instances
     * with an {@code AND} operation.
     * 
     * <p>
     * The instance evaluates to {@code true} iff all base logic instances also
     * evaluate to {@code true}. If any logic instance evaluates to
     * {@code false} the combined logic evaluates to {@code false}.
     * </p>
     * 
     * @param logic0
     *            the first logic instance
     * @param logics
     *            the remaining logic instances
     * @return a combined logic instance
     * @throws NullPointerException
     *             if any parameter is {@code null}, or the {@code logics} array
     *             contains {@code null} values.
     * @throws IllegalArgumentException
     *             if the {@code logics} array is empty
     */
    static Logic and(Logic logic0, Logic... logics) {
        Objects.requireNonNull(logic0, "no logic instance may be null!");
        Objects.requireNonNull(logics, "the logics array may not be null!");
        for (Logic l : logics)
            Objects.requireNonNull(l, "no logic instance may be null!");
        if (logics.length == 0)
            throw new IllegalArgumentException("logics array may not be empty!");
        // code
        return () -> {
            if (!logic0.evaluate())
                return false;
            for (Logic logic : logics)
                if (!logic.evaluate())
                    return false;
            return true;
        };
    }

    /**
     * Creates a logic instance that counts how many base instances evaluate to
     * {@code true}. The instance evaluates to {@code true} iff the number of
     * base instances that evaluate to {@code true} matches the given
     * {@code target} parameter.
     * 
     * @param target
     *            the exact number of logic instances that must evaluate to
     *            {@code true} for this instance to evaluate to {@code true}.
     * @param logics
     *            the base logic instances
     * @return the combined logic instances
     * @throws NullPointerException
     *             if the logics array is {@code null} or any of its elements is
     *             {@code null}
     * @throws IllegalArgumentException
     *             if the logics array is empty
     * @throws IllegalArgumentException
     *             if the {@code target} value is negative or larger than the
     *             number of elements in the logics array. Both of these
     *             conditions would result in the instance evaluating to
     *             {@code false} and are therefore prohibited.
     */
    static Logic count(int target, Logic... logics) {
        Objects.requireNonNull(logics, "the logics array may not be null!");
        for (Logic l : logics)
            Objects.requireNonNull(l, "no logic instance may be null!");
        if (logics.length == 0)
            throw new IllegalArgumentException("logics array may not be empty!");
        if (target < 0)
            throw new IllegalArgumentException("the target value may not be negative!");
        if (target > logics.length)
            throw new IllegalArgumentException("the target value may not exceed the size of the logics array");
        // code
        return () -> {
            int counter = 0;
            for (Logic logic : logics)
                if (logic.evaluate())
                    counter++;
            return counter == target;
        };
    }

    /**
     * Creates a logic instance that evaluates to {@code true} iff all base
     * instances evaluate to {@code false}.
     * 
     * @param logics
     *            the base logics
     * @return a combined logic instance
     * @throws NullPointerException
     *             if the logics array is {@code null} or any of its elements
     *             are {@code null}
     * @throws IllegalArgumentException
     *             if the logics array is empty
     */
    static Logic none(Logic... logics) {
        return any(logics).not();
    }

    /**
     * Creates a logic instance that evaluates to {@code true} iff all base
     * instance evaluate to {@code true}. This is essentially identical to an
     * {@code AND} operation over all base logics.
     * 
     * @param logics
     *            the base logics
     * @return a combined logic instance
     * @throws NullPointerException
     *             if the logics array is {@code null} or any of its elements
     *             are {@code null}
     * @throws IllegalArgumentException
     *             if the logics array is empty
     */
    static Logic all(Logic... logics) {
        return and(TRUE, logics);
    }

    /**
     * Creates a logic instance that evaluates to {@code true} if any base
     * instance evaluates to {@code true}. This is essentially identical to an
     * {@code OR} operation over all base logics.
     * 
     * @param logics
     *            the base logics
     * @return a combined logic instance
     * @throws NullPointerException
     *             if the logics array is {@code null} or any of its elements
     *             are {@code null}
     * @throws IllegalArgumentException
     *             if the logics array is empty
     */
    static Logic any(Logic... logics) {
        return or(FALSE, logics);
    }

    /**
     * Creates a logic instance with inverted logic. This inverted instance
     * evaluates to {@code true} if the base instance evaluates to {@code false}
     * (and vice versa).
     * 
     * @return an instance with inverted logic
     * @throws NullPointerException
     *             if the logic parameter is {@code null}
     */
    static Logic not(Logic logic) {
        return logic.not();
    }

    /**
     * Creates an edge triggered logic instance.
     * 
     * This new instance only evaluates to {@code true} the first time the base
     * {@link Logic} evaluates to {@code true} after previously evaluating to
     * {@code false}. This emulates edge triggering on a rising edge.
     * 
     * @param logic
     *            the instance to base the edge triggering on
     * @return an edge triggering {@link Logic} instance
     * @throws NullPointerException
     *             if the logic parameter is {@code null}
     */
    static Logic edge(Logic logic) {
        return logic.edge();
    }

}
