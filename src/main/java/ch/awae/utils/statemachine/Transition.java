package ch.awae.utils.statemachine;

import java.util.Objects;

final class Transition {

    final String    origin;
    final String    event;
    final String    target;
    final Command[] commands;

    Transition(String origin, String event, String target, Command... commands) {
        this.origin = Objects.requireNonNull(origin, "origin may not be null");
        this.event = Objects.requireNonNull(event, "event may not be null");
        this.target = Objects.requireNonNull(target, "target may not be null");
        this.commands = Objects.requireNonNull(commands, "commands may not be null");
        for (int i = 0; i < commands.length; i++)
            Objects.requireNonNull(commands[i], "commands[" + i + "] may not be null");
    }

}
