package ch.awae.utils.statemachine;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

final class StateMachineImpl implements StateMachine {

    private final MachineCore[] cores;

    private final BlockingQueue<String> eventQueue;
    private final BlockingQueue<String> internalEventQueue;
    private final BlockingQueue<String> commandQueue;

    private final Object LOCK   = new Object();
    private Thread       worker = null;

    private final Logger logger;
    private final String uuid;

    StateMachineImpl(String uuid, boolean priority, Logger logger, MachineCore... cores) {
        this.uuid = uuid;
        this.logger = logger;
        // load cores
        this.cores = Objects.requireNonNull(cores, "cores may not be null");
        for (int i = 0; i < cores.length; i++)
            Objects.requireNonNull(cores[i], "core[" + i + "] may not be null");
        // create queues
        eventQueue = new LinkedBlockingQueue<>();
        commandQueue = new LinkedBlockingQueue<>();
        if (priority) {
            logger.finer(uuid + ": internal events are handled with priority");
            internalEventQueue = new LinkedBlockingQueue<>();
        } else {
            logger.finer(uuid + ": internal events are handled normally");
            internalEventQueue = null;
        }
        logger.finer(uuid + ": finished initialisation");

        // log configuration summary
        StringBuilder sb = new StringBuilder(uuid + ": configuration summary:\n");
        sb.append("========================================================\n");
        sb.append("machine ID:         " + uuid + "\n");
        sb.append("event handling:     " + (internalEventQueue == null ? "normal" : "priority") + "\n");
        sb.append("core count:         " + cores.length);
        for (MachineCore core : cores) {
            sb.append("\n\n" + core.coreSummary());
        }
        sb.append("\n========================================================");
        logger.config(sb.toString());
    }

    @Override
    public void event(String event) {
        logger.finer(uuid + ": received event: " + event);
        eventQueue.add(Objects.requireNonNull(event, "event may not be null"));
    }

    @Override
    public BlockingQueue<String> getCommandQueue() {
        return commandQueue;
    }

    @Override
    public void start() {
        synchronized (LOCK) {
            if (worker != null)
                throw new IllegalStateException("worker already running");
            worker = new Thread(this::processingLoop);
            logger.fine(uuid + ": starting worker thread");
            worker.start();
        }
    }

    @Override
    public void stop() {
        synchronized (LOCK) {
            if (worker == null)
                throw new IllegalStateException("worker not running");
            logger.fine(uuid + ": stopping worker thread");
            worker.interrupt();
            // wait for the thread to die off
            boolean interrupted = false;
            while (true) {
                try {
                    worker.join();
                    break;
                } catch (InterruptedException e) {
                    // cache interrupt and try again
                    interrupted = true;
                }
            }
            worker = null;
            // re-apply interrupt
            if (interrupted)
                Thread.currentThread().interrupt();
        }
    }

    @Override
    public void reset() {
        synchronized (LOCK) {
            logger.fine(uuid + ": resetting state machine");
            boolean running = worker != null;
            if (running)
                stop();
            for (MachineCore core : cores)
                core.reset();
            eventQueue.clear();
            if (internalEventQueue != null)
                internalEventQueue.clear();
            if (running)
                start();
            logger.finer(uuid + ": reset complete");
        }
    }

    private void processingLoop() {
        try {
            while (!Thread.interrupted()) {
                // take the next event

                String event;

                if (internalEventQueue != null && !internalEventQueue.isEmpty())
                    event = internalEventQueue.take();
                else
                    event = eventQueue.take();

                logger.finer(uuid + ": processing event: " + event);
                // feed the event into all cores
                for (MachineCore core : cores) {
                    for (Command command : core.processEvent(event)) {
                        // process all commands
                        switch (command.type) {
                            case EVENT:
                                logger.finest(uuid + ": issuing internal event: " + command.command);
                                if (internalEventQueue != null)
                                    internalEventQueue.add(command.command);
                                else
                                    eventQueue.add(command.command);
                                break;
                            case COMMAND:
                                logger.finest(uuid + ": issuing command: " + command.command);
                                commandQueue.add(command.command);
                                break;
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            return;
        }
    }

    @Override
    public String extractDiagram() {
        StringBuilder builder = new StringBuilder();
        final char br = '\n';
        // header
        builder.append("digraph {" + br);
        // all cores
        for (int i = 0; i < cores.length; i++) {
            // build core
            builder.append(cores[i].graphSection(i));
        }
        // finish
        builder.append("}");
        return builder.toString();
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public StateMachine.SavedState getCurrentState() {
        synchronized (LOCK) {
            logger.fine(uuid + ": creating snapshot");
            boolean running = worker != null;
            if (running)
                stop();
            logger.finer(uuid + ": reading core states");
            String[] states = new String[cores.length];
            StringBuilder sb = new StringBuilder(uuid + ": core dump:\n====================================\n" + uuid);
            for (int i = 0; i < cores.length; i++) {
                states[i] = cores[i].getState();
                sb.append("\n[" + i + "] " + states[i]);
            }
            sb.append("\n====================================");
            logger.fine(sb.toString());
            logger.finer(uuid + ": core dump complete");
            if (running)
                start();
            logger.finer(uuid + ": snapshot complete");
            return new SavedState(uuid, states);
        }
    }

    @Override
    public void loadState(StateMachine.SavedState state, boolean clear) {
        Objects.requireNonNull(state, "'state' may not be null");
        if (!(state instanceof SavedState))
            throw new IllegalArgumentException("save type not compatible with instance");
        SavedState save = (SavedState) state;
        if (!save.uuid.equals(uuid))
            throw new IllegalArgumentException("save uuid mismatch: read: " + save.uuid + " expected: " + uuid);
        logger.fine(uuid + ": reloading states " + (clear ? "with" : "without") + " event queue reset");
        // prepare load
        synchronized (LOCK) {
            boolean running = worker != null;
            if (running)
                stop();
            // start load
            if (clear) {
                reset();
            }
            logger.fine(uuid + ": loading cores");
            for (int i = 0; i < cores.length; i++) {
                cores[i].setState(save.states[i]);
            }
            logger.finer(uuid + ": completed core load");
            // end load
            if (running)
                start();
        }
        logger.finer(uuid + ": completed state reload");
    }

    private static class SavedState implements StateMachine.SavedState {

        final String   uuid;
        final String[] states;

        SavedState(String uuid, String[] states) {
            this.uuid = uuid;
            this.states = states;
        }

        @Override
        public String getUUID() {
            return uuid;
        }

    }

}
