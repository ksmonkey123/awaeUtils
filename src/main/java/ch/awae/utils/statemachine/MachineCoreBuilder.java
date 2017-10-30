package ch.awae.utils.statemachine;

import java.util.ArrayList;

public final class MachineCoreBuilder {

    private ArrayList<Transition> transitions  = new ArrayList<>();
    private String                initialState = null;

    public MachineCoreBuilder addTransition(String from, String event, String to, String[] events, String[] commands) {
        Command[] cmds = new Command[events.length + commands.length];

        for (int i = 0; i < events.length; i++)
            cmds[i] = new Command(CommandType.EVENT, events[i]);

        for (int i = 0; i < commands.length; i++)
            cmds[i + events.length] = new Command(CommandType.COMMAND, commands[i]);

        transitions.add(new Transition(from, event, to, cmds));
        return this;
    }

    public MachineCoreBuilder setInitialState(String state) {
        initialState = state;
        return this;
    }

    MachineCore build() {
        return new MachineCore(initialState, transitions.toArray(new Transition[0]));
    }

}
