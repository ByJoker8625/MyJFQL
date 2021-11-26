package de.byjoker.myjfql.command;

import java.util.List;

public interface CommandService {

    void registerCommand(Command command);

    void unregisterCommand(Command command);

    void searchCommands(String path);

    boolean existsCommand(String name);

    void execute(CommandSender sender, String name);

    Command getCommand(String name);

    List<Command> getCommands();

}
