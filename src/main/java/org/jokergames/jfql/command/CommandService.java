package org.jokergames.jfql.command;

import org.jokergames.jfql.command.executor.ConsoleExecutor;
import org.jokergames.jfql.command.executor.Executor;
import org.jokergames.jfql.command.executor.RemoteExecutor;
import org.jokergames.jfql.core.JFQL;
import org.jokergames.jfql.event.CommandExecuteEvent;
import org.jokergames.jfql.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Janick
 */

public class CommandService {

    private final List<Command> commands;

    public CommandService() {
        this.commands = new ArrayList<>();
    }

    public boolean execute(User user, Executor executor, Map<String, List<String>> arguments) {
        if (arguments == null) {
            if (executor instanceof RemoteExecutor) {
                ((RemoteExecutor) executor).sendError("Command was not found!");
            } else {
                ((ConsoleExecutor) executor).sendError("Command was not found!");
            }

            JFQL.getInstance().getEventService().callEvent(CommandExecuteEvent.TYPE, new CommandExecuteEvent(executor, user, null));
            return false;
        }

        if (arguments.size() != 0 && arguments.get("COMMAND").get(0).equals("#")) {
            return false;
        }

        JFQL.getInstance().getEventService().callEvent(CommandExecuteEvent.TYPE, new CommandExecuteEvent(executor, user, JFQL.getInstance().getFormatter().formatString(arguments.get("COMMAND"))));
        return getCommand(arguments.get("COMMAND").get(0)).handle(executor, arguments, user);
    }

    public void execute(Map<String, List<String>> arguments) {
        try {
            execute(JFQL.getInstance().getConsoleUser(), new ConsoleExecutor(), arguments);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Command getCommand(String s) {
        return commands.stream().filter(command -> command.getName().equalsIgnoreCase(s) || command.getAliases().contains(s.toUpperCase())).findFirst().orElse(null);
    }

    public void execute(String command) {
        try {
            execute(JFQL.getInstance().getFormatter().formatCommand(command));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void registerCommand(Command command) {
        commands.add(command);
    }

    public void unregisterCommand(Command command) {
        commands.remove(command);
    }

    public List<Command> getCommands() {
        return commands;
    }
}
