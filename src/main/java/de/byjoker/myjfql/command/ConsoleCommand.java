package de.byjoker.myjfql.command;

import java.util.List;
import java.util.Map;

public abstract class ConsoleCommand extends Command {

    public ConsoleCommand(String name, List<String> syntax) {
        super(name, syntax);
    }

    public abstract void handleConsoleCommand(ConsoleCommandSender sender, final Map<String, List<String>> args);

    @Override
    public final void handleCommand(CommandSender sender, Map<String, List<String>> args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendForbidden();
            return;
        }

        handleConsoleCommand((ConsoleCommandSender) sender, args);
    }
}
