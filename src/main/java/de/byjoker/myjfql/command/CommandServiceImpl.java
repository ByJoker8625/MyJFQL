package de.byjoker.myjfql.command;

import de.byjoker.myjfql.exception.LanguageException;
import de.byjoker.myjfql.lang.CommandFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.reflect.ClassPath.ClassInfo;
import static com.google.common.reflect.ClassPath.from;

public class CommandServiceImpl implements CommandService {

    private final List<Command> commands;
    private final CommandFormatter formatter;

    public CommandServiceImpl(CommandFormatter formatter) {
        this.commands = new ArrayList<>();
        this.formatter = formatter;
    }

    @Override
    public void registerCommand(Command command) {
        commands.add(command);
    }

    @Override
    public void unregisterCommand(Command command) {
        commands.remove(command);
    }

    @Override
    public void searchCommands(String path) {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try {
            for (ClassInfo info : from(loader).getTopLevelClasses()) {
                if (info.getName().startsWith(path)) {
                    try {
                        final Class<? extends Command> clazz = (Class<? extends Command>) info.load();

                        if (clazz.isAnnotationPresent(CommandHandler.class))
                            registerCommand(clazz.newInstance());
                    } catch (Exception ignore) {
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean existsCommand(String name) {
        return commands.stream().anyMatch(command -> command.getName().equalsIgnoreCase(name));
    }

    @Override
    public Command getCommand(String name) {
        return commands.stream().filter(command -> command.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public void execute(CommandSender sender, String name) {
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

            command.execute(sender, arguments);
        } catch (Exception ex) {
            new LanguageException(ex).printStackTrace();
        }
    }

    @Override
    public List<Command> getCommands() {
        return commands;
    }

}
