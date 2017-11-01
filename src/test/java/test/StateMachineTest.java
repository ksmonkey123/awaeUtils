package test;

import ch.awae.utils.statemachine.MachineCoreBuilder;
import ch.awae.utils.statemachine.StateMachine;
import ch.awae.utils.statemachine.StateMachineBuilder;

public class StateMachineTest {

    public static void main(String[] args) throws InterruptedException {
        MachineCoreBuilder b1 = new MachineCoreBuilder();
        b1.setInitialState("a");
        b1.addArbitrarySequence("a", new String[] { "1", "2", "3", "4", "5" }, "a", null, new String[] { "c -> a" });

        StateMachineBuilder builder = new StateMachineBuilder();
        builder.addMachineCore(b1);

        StateMachine machine = builder.build();

        System.out.println(machine.extractDiagram());
    }

}
