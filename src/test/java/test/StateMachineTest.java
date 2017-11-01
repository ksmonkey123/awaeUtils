package test;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.awae.utils.statemachine.MachineCoreBuilder;
import ch.awae.utils.statemachine.StateMachine;
import ch.awae.utils.statemachine.StateMachine.SavedState;
import ch.awae.utils.statemachine.StateMachineBuilder;

public class StateMachineTest {

    static {
        Logger logger = Logger.getLogger("");
        for (Handler handler : logger.getHandlers()) {
            handler.setLevel(Level.ALL);
        }
        logger.setLevel(Level.CONFIG);
    }

    public static void main(String[] args) throws InterruptedException {

        MachineCoreBuilder b1 = new MachineCoreBuilder();

        b1.setInitialState("off");

        b1.addTransition("off", "activate", "on", new String[] { "x" }, new String[] { "started" });
        b1.addTransition("on", "turbo", "turbo", null, new String[] { "activated turbo" });
        b1.addTransition("turbo", "no turbo", "on", null, new String[] { "stopped turbo" });
        b1.addTransition("on", "disable", "off", new String[] { "y" }, new String[] { "disabled" });
        b1.addTransition("turbo", "disable", "off", new String[] { "y" }, new String[] { "disabled" });

        MachineCoreBuilder b2 = new MachineCoreBuilder();

        b2.setInitialState("off");

        b2.addTransition("off", "x", "on", null, new String[] { "someone activated" });
        b2.addTransition("on", "y", "off", null, new String[] { "someone deactivated" });
        b2.addArbitrarySequence("a", new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }, "b", null, null);
        b2.setAllowTerminalStates(true);

        StateMachineBuilder b = new StateMachineBuilder();

        b.addMachineCore(b1);
        b.addMachineCore(b2);
        b.setPrioritiseInternalEvents(true);

        StateMachine machine = b.build();

       // System.out.println(machine.extractDiagram());

        System.exit(0);

        machine.start();

        machine.event("activate");
        machine.event("turbo");

        Thread.sleep(1000);

        SavedState state = machine.getCurrentState();

        machine.event("disable");
        machine.event("disable");

        Thread.sleep(1000);

        machine.loadState(state, true);

        machine.event("disable");

    }

}
