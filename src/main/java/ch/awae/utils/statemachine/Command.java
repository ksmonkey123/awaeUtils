package ch.awae.utils.statemachine;

import java.util.Objects;

final class Command {

    final CommandType type;
    final String      command;

    Command(CommandType type, String command) {
        this.type = Objects.requireNonNull(type, "type may not be null");
        this.command = Objects.requireNonNull(command, "command may not be null");
    }

}
