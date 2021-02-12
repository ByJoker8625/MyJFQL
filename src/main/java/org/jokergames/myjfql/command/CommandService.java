package org.jokergames.myjfql.command;

import org.jokergames.myjfql.command.executor.ConsoleExecutor;
import org.jokergames.myjfql.command.executor.Executor;
import org.jokergames.myjfql.command.executor.RemoteExecutor;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.event.CommandExecuteEvent;
import org.jokergames.myjfql.user.User;

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

            MyJFQL.getInstance().getEventService().callEvent(CommandExecuteEvent.TYPE, new CommandExecuteEvent(executor, user, null));
            return false;
        }

        if (arguments.size() != 0 && arguments.get("COMMAND").get(0).equals("#")) {
            return false;
        }

        MyJFQL.getInstance().getEventService().callEvent(CommandExecuteEvent.TYPE, new CommandExecuteEvent(executor, user, MyJFQL.getInstance().getFormatter().formatString(arguments.get("COMMAND"))));
        return getCommand(arguments.get("COMMAND").get(0)).handle(executor, arguments, user);
    }

    public void execute(Map<String, List<String>> arguments) {
        try {
            execute(MyJFQL.getInstance().getConsoleUser(), new ConsoleExecutor(), arguments);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Command getCommand(String s) {
        return commands.stream().filter(command -> command.getName().equalsIgnoreCase(s) || command.getAliases().contains(s.toUpperCase())).findFirst().orElse(null);
    }

    public void execute(String command) {
        try {
            execute(MyJFQL.getInstance().getFormatter().formatCommand(command));
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
