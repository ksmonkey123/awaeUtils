package test;

import ch.awae.utils.statemachine.MachineCoreBuilder;
import ch.awae.utils.statemachine.StateMachine;
import ch.awae.utils.statemachine.StateMachineBuilder;

public class StateMachineTest {

    public static void main(String[] args) throws InterruptedException {
        MachineCoreBuilder b1 = new MachineCoreBuilder();
        b1.addTransition("a", "1", "b", new String[] { "3" }, new String[] { "a -> b" });
        b1.addTransition("b", "2", "a", new String[] { "4" }, new String[] { "b -> a" });
        b1.setInitialState("a");

        MachineCoreBuilder b2 = new MachineCoreBuilder();
        b2.addTransition("c", "3", "d", null, new String[] { "c -> d" });
        b2.addTransition("d", "4", "c", null, new String[] { "d -> c" });
        b2.setInitialState("c");

        StateMachineBuilder builder = new StateMachineBuilder();
        builder.addMachineCore(b1);
        builder.addMachineCore(b2);

        StateMachine machine = builder.build();

        Thread t = new Thread(() -> {
            while (!Thread.interrupted())
                try {
                    System.out.println(machine.getCommandQueue().take());
                } catch (InterruptedException e) {
                    return;
                }
        });
        t.start();
        machine.start();

        String[] events = { "1", "2", "1", "1", "2", "3", "1", "2", "1" };

        for (String event : events) {
            System.out.println("> " + event);
            machine.event(event);
            Thread.sleep(200);
        }
        t.interrupt();
        machine.stop();
    }

}
