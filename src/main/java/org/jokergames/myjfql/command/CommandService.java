package org.jokergames.myjfql.command;

import org.jokergames.myjfql.core.lang.Formatter;
import org.jokergames.myjfql.exception.CommandException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandService {

    private final List<Command> commands;
    private final Formatter formatter;

    public CommandService() {
        this.commands = new ArrayList<>();
        this.formatter = new Formatter();
    }

    public void register(final Command command) {
        commands.add(command);
    }

    public void unregister(final Command command) {
        commands.remove(command);
    }

    public boolean isRegistered(final String name) {
        return commands.stream().anyMatch(command -> command.getName().equalsIgnoreCase(name));
    }

    public Command getCommand(final String name) {
        return commands.stream().filter(command -> command.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void execute(final CommandSender sender, final String name) {
        try {
            final Map<String, List<String>> arguments = formatter.formatCommand(name);

            if (arguments == null) {
                sender.sendError("Command was not found!");
                return;
            }

            final Command command = getCommand(arguments.get("COMMAND").get(0));

            if (command == null) {
                sender.sendError("Command was not found!");
                return;
            }

            command.handle(sender, arguments);
        } catch (Exception ex) {
            new CommandException(ex).printStackTrace();
        }
    }

    public List<Command> getCommands() {
        return commands;
    }

    public Formatter getFormatter() {
        return formatter;
    }
}
