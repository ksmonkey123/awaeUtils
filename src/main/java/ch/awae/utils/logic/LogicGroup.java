package ch.awae.utils.logic;

import java.util.Arrays;
import java.util.Objects;

/**
 * Groups a list of {@link Logic} instances. A LogicGroup must consist of at
 * least one member. This is enforced during creation.
 * 
 * @author Andreas Wälchli
 * 
 * @see Logic
 */
public final class LogicGroup {

    private final Logic members[];

    /**
     * Creates a new Logic group
     * 
     * @param members
     *            the group members
     * @throws NullPointerException
     *             if the members array is {@code null} or contains any
     *             {@code null} elements
     * @throws IllegalArgumentException
     *             if the members array is empty
     */
    public LogicGroup(Logic[] members) {
        Objects.requireNonNull(members, "the members array may not be null!");
        for (Logic l : members)
            Objects.requireNonNull(l, "no logic instance may be null!");
        if (members.length == 0)
            throw new IllegalArgumentException("the members array may not be empty!");
        this.members = members;
    }

    /**
     * Provides an array containing all group members
     * 
     * @return an array of all members
     */
    public Logic[] toArray() {
        return Arrays.copyOf(members, members.length);
    }

    /**
     * Creates a Logic instance over the group, defined by
     * {@link Logic#any(Logic...)}.
     * 
     * @return the created logic instance
     */
    public Logic any() {
        return Logic.any(members);
    }

    /**
     * Creates a Logic instance over the group, defined by
     * {@link Logic#all(Logic...)}.
     * 
     * @return the created logic instance
     */
    public Logic all() {
        return Logic.all(members);
    }

    /**
     * Creates a Logic instance over the group, defined by
     * {@link Logic#none(Logic...)}.
     * 
     * @return the created logic instance
     */
    public Logic none() {
        return Logic.none(members);
    }

    /**
     * Creates a Logic instance over the group, defined by
     * {@link Logic#count(int, Logic...)}.
     * 
     * @param target
     *            the exact number of group members to evaluate to {@code true}
     *            for the created Logic instance to evaluate to {@code true}
     * 
     * @return the created logic instance
     * @throws IllegalArgumentException
     *             if the target value is negative or larger than the group size
     * 
     * @see Logic#count(int, Logic...)
     */
    public Logic count(int target) {
        return Logic.count(target, members);
    }

    /**
     * @return the size of the group
     */
    public int size() {
        return members.length;
    }

    /**
     * Merges this LogicGroup with another one and returns a new LogicGroup
     * containing the members from both this and the other group.
     * 
     * @param other
     * @return
     */
    public LogicGroup merge(LogicGroup other) {
        Objects.requireNonNull(other);
        int size = members.length + other.members.length;
        Logic[] children = new Logic[size];
        System.arraycopy(members, 0, children, 0, members.length);
        System.arraycopy(other.members, 0, children, members.length, other.members.length);
        return new LogicGroup(children);
    }

    public Logic strict(LogicGroup domain) {
        return all().and(domain.count(size()));
    }

}
