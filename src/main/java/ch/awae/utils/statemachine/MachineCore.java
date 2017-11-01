package ch.awae.utils.statemachine;

import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

final class MachineCore {

    private final static Command[] EMPTY_COMMAND_ARRAY = new Command[0];

    private final HashMap<String, HashMap<String, Transition>> map;

    private final String initialState;
    private String       currentState;

    // logging support
    private final Logger  logger;
    private final String  prefix;
    private final int     transitionCount;
    private final boolean strict;
    private final boolean checked;
    private final int     coreID;

    MachineCore(int id, String logtitle, Logger logger, boolean strict, boolean checked, String initial,
            Transition... transitions) {
        prefix = logtitle + ": ";
        this.strict = strict;
        this.checked = checked;
        coreID = id;
        this.logger = Objects.requireNonNull(logger, "logger may not be null");
        this.initialState = Objects.requireNonNull(initial, "initial may not be null");
        this.currentState = this.initialState;
        this.transitionCount = transitions.length;
        this.map = new HashMap<>();
        logger.finest(prefix + (strict ? "does not allow" : "allows") + " for terminal states");
        // fill the map
        logger.finer(prefix + "adding " + transitions.length + " transitions");
        for (int i = 0; i < transitions.length; i++) {
            final Transition transition = Objects.requireNonNull(transitions[i],
                    "transitions[" + i + "] may not be null");
            logger.finest(prefix + "adding transition " + (i + 1) + "/" + transitions.length + ": " + transition.event
                    + ": " + transition.origin + " -> " + transition.target);
            // access transition map for state
            HashMap<String, Transition> temp;
            if (this.map.containsKey(transition.origin))
                temp = this.map.get(transition.origin);
            else {
                temp = new HashMap<>();
                this.map.put(transition.origin, temp);
            }

            // register transition
            if (temp.containsKey(transition.event))
                throw new IllegalArgumentException(
                        "duplicate transition event '" + transition.event + "' on state '" + transition.origin + "'");
            temp.put(transition.event, transition);
        }
        logger.finer(prefix + "validating transitions");
        // confirm that all transition targets exist
        if (checked)
            this.map.forEach((origin, map) -> map.forEach((event, transition) -> {
                if (!this.map.containsKey(transition.target)) {
                    if (strict) {
                        logger.severe("transition '" + event + "' on state '" + origin + "' leads to terminal state '"
                                + transition.target + "'");
                        throw new IllegalArgumentException("transition '" + event + "' on state '" + origin
                                + "' leads to terminal state '" + transition.target + "'");
                    } else {
                        logger.warning("transition '" + event + "' on state '" + origin + "' leads to terminal state '"
                                + transition.target + "'");
                    }
                }
            }));
        else {
            if (strict)
                logger.warning(prefix + "terminal state check is disabled but terminal states are not allowed");
            else
                logger.warning(prefix + "terminal state check is disabled");
        }
        logger.finer(prefix + "validating initial state " + initialState);
        // confirm that initial state is well-defined
        if (!this.map.containsKey(this.initialState))
            throw new IllegalArgumentException("unknown initial state '" + this.initialState + "'");
        logger.finer(prefix + "loaded " + map.size() + " states and " + transitions.length + " transitions");
    }

    synchronized String getState() {
        return this.currentState;
    }

    synchronized void setState(String state) {
        logger.fine(prefix + "forced to switch to state " + state);
        this.currentState = state;
    }

    synchronized Command[] processEvent(String event) {
        logger.finer(prefix + "processing event: " + event);
        // find transition
        HashMap<String, Transition> map = this.map.get(this.currentState);
        if (map == null) {
            logger.finer(prefix + "core is in terminal state. ignoring event");
            return EMPTY_COMMAND_ARRAY;
        }
        Transition transition = map.get(Objects.requireNonNull(event, "event may not be null"));
        // ignore if there's no applicable transition
        if (transition == null) {
            logger.finer(prefix + "no transition found for event " + event + " on state " + currentState
                    + ". ignoring event");
            return EMPTY_COMMAND_ARRAY;
        }
        // process transition
        logger.fine(prefix + "state change (" + event + "): " + currentState + " -> " + transition.target);
        this.currentState = transition.target;
        logger.finer(prefix + "state change yielded " + transition.commands.length + " commands");
        return transition.commands;
    }

    synchronized void reset() {
        logger.fine(prefix + "resetting to state " + initialState);
        this.currentState = this.initialState;
    }

    int getStateCount() {
        return map.size();
    }

    int getTransitionCount() {
        return this.transitionCount;
    }

    String coreSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("> core ID:          " + coreID + "\n");
        sb.append("> states:           " + getStateCount() + "\n");
        sb.append("> transitions:      " + getTransitionCount() + "\n");
        sb.append("> initial state:    " + initialState + "\n");
        sb.append("> allows terminals: " + (strict ? "no" : "yes") + "\n");
        sb.append("> terminals check:  " + (checked ? strict ? "full" : "warning only" : "disabled"));
        return sb.toString();
    }

    String graphSection(int index) {
        StringBuilder builder = new StringBuilder();
        // initial node
        builder.append("\"" + index + "." + initialState + "\" [peripheries=2]\n");
        // all transitions
        for (HashMap<String, Transition> map : this.map.values()) {
            for (Transition t : map.values()) {
                builder.append("\"" + index + "." + t.origin + "\" -> \"" + index + "." + t.target + "\" [label=\""
                        + t.event + "\"]\n");
            }
        }
        // finish
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + coreID;
        result = prime * result + ((initialState == null) ? 0 : initialState.hashCode());
        result = prime * result + ((map == null) ? 0 : map.hashCode());
        result = prime * result + (strict ? 1231 : 1237);
        result = prime * result + transitionCount;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MachineCore)) {
            return false;
        }
        MachineCore other = (MachineCore) obj;
        if (coreID != other.coreID) {
            return false;
        }
        if (initialState == null) {
            if (other.initialState != null) {
                return false;
            }
        } else if (!initialState.equals(other.initialState)) {
            return false;
        }
        if (map == null) {
            if (other.map != null) {
                return false;
            }
        } else if (!map.equals(other.map)) {
            return false;
        }
        if (strict != other.strict) {
            return false;
        }
        if (transitionCount != other.transitionCount) {
            return false;
        }
        return true;
    }

}
