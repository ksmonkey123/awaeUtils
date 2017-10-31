package ch.awae.utils.statemachine;

import java.util.HashMap;
import java.util.Objects;

final class MachineCore {

    private final static Command[] EMPTY_COMMAND_ARRAY = new Command[0];

    private final HashMap<String, HashMap<String, Transition>> map;

    private final String initialState;
    private String       currentState;

    MachineCore(String initial, Transition... transitions) {
        this.initialState = Objects.requireNonNull(initial, "initial may not be null");
        this.currentState = this.initialState;
        this.map = new HashMap<>();
        // fill the map
        for (int i = 0; i < transitions.length; i++) {
            final Transition transition = Objects.requireNonNull(transitions[i],
                    "transitions[" + i + "] may not be null");
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
        // confirm that all transition targets exist
        this.map.forEach((origin, map) -> map.forEach((event, transition) -> {
            if (!this.map.containsKey(transition.target))
                throw new IllegalArgumentException("transition '" + event + "' on state '" + origin
                        + "' has unknown target state '" + transition.target + "'");
        }));
        // confirm that initial state is well-defined
        if (!this.map.containsKey(this.initialState))
            throw new IllegalArgumentException("unknown initial state '" + this.initialState + "'");
    }

    synchronized Command[] processEvent(String event) {
        // find transition
        Transition transition = this.map //
                .get(this.currentState) //
                .get(Objects.requireNonNull(event, "event may not be null"));
        // ignore if there's no applicable transition
        if (transition == null)
            return EMPTY_COMMAND_ARRAY;
        // process transition
        this.currentState = transition.target;
        return transition.commands;
    }

    synchronized void reset() {
        this.currentState = this.initialState;
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

}
