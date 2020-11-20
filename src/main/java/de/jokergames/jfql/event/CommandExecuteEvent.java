package de.jokergames.jfql.event;

import de.jokergames.jfql.command.executor.Executor;
import de.jokergames.jfql.user.User;

/**
 * @author Janick
 */

public class CommandExecuteEvent extends Event {

    public static final String TYPE = "CommandExecuteEvent";
    private final Executor executor;
    private final User user;
    private final String command;

    public CommandExecuteEvent(Executor executor, User user, String command) {
        super(TYPE);
        this.executor = executor;
        this.user = user;
        this.command = command;
    }

    public Executor getExecutor() {
        return executor;
    }

    public User getUser() {
        return user;
    }

    public String getCommand() {
        return command;
    }
}
