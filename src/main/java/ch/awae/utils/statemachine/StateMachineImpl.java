package ch.awae.utils.statemachine;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

final class StateMachineImpl implements StateMachine {

    private final MachineCore[] cores;

    private final BlockingQueue<String> eventQueue;
    private final BlockingQueue<String> commandQueue;

    private final Object LOCK = new Object();

    private Thread worker = null;

    StateMachineImpl(MachineCore... cores) {
        // load cores
        this.cores = Objects.requireNonNull(cores, "cores may not be null");
        for (int i = 0; i < cores.length; i++)
            Objects.requireNonNull(cores[i], "core[" + i + "] may not be null");
        // create queues
        eventQueue = new LinkedBlockingQueue<>();
        commandQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void event(String event) {
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
            worker.start();
        }
    }

    @Override
    public void stop() {
        synchronized (LOCK) {
            if (worker == null)
                throw new IllegalStateException("worker not running");
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
            // cleanup
            worker = null;
            // re-apply interrupt
            if (interrupted)
                Thread.currentThread().interrupt();
        }
    }

    @Override
    public void reset() {
        synchronized (LOCK) {
            boolean running = worker != null;
            if (running)
                stop();
            for (MachineCore core : cores)
                core.reset();
            eventQueue.clear();
            if (running)
                start();
        }
    }

    private void processingLoop() {
        try {
            while (!Thread.interrupted()) {
                // take the next event
                String event = eventQueue.take();
                // feed the event into all cores
                for (MachineCore core : cores) {
                    for (Command command : core.processEvent(event)) {
                        // process all commands
                        switch (command.type) {
                            case EVENT:
                                eventQueue.add(command.command);
                                break;
                            case COMMAND:
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

}
