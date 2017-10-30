package ch.awae.utils.statemachine;

import java.util.ArrayList;

public class StateMachineBuilder {

    private ArrayList<MachineCoreBuilder> cores = new ArrayList<>();

    public StateMachineBuilder addMachineCore(MachineCoreBuilder builder) {
        cores.add(builder);
        return this;
    }

    public StateMachine build() {
        MachineCore[] cores = new MachineCore[this.cores.size()];
        for (int i = 0; i < cores.length; i++)
            cores[i] = this.cores.get(i).build();
        return new StateMachineImpl(cores);
    }
}
