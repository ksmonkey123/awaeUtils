package test;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.awae.utils.statemachine.MachineCoreBuilder;
import ch.awae.utils.statemachine.StateMachine;
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

        MachineCoreBuilder b1 = new MachineCoreBuilder("private");

        b1.setInitialState("a");
        b1.addArbitrarySequence("a", new String[] { "1", "2", "3", "4" }, "b");
        b1.addTransition("b", "off", "a");
        b1.setCheckForTerminalStates(false);

        StateMachine machine = new StateMachineBuilder().addMachineCore(b1).build();

        System.out.println(machine.extractDiagram());

    }

}
