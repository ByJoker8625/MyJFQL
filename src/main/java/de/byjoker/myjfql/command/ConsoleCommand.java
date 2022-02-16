package de.byjoker.myjfql.command;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public abstract class ConsoleCommand extends Command {

    public ConsoleCommand(String name, List<String> syntax) {
        super(name, syntax);
    }

    public abstract void executeAsConsole(ConsoleCommandSender sender, @NotNull Map<String, ? extends List<String>> args);

    @Override
    public final void execute(@NotNull CommandSender sender, @NotNull Map<String, ? extends List<String>> args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendForbidden();
            return;
        }

        if (sender.getSession() == null) {
            sender.sendError("Session of this user is invalid!");
            return;
        }

        executeAsConsole((ConsoleCommandSender) sender, args);
    }
}
