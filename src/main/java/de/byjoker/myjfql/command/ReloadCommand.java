package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@CommandExecutor
public class ReloadCommand extends ConsoleCommand {

    public ReloadCommand() {
        super("reload", Arrays.asList("COMMAND", "CONFIG", "DATABASES", "SERVER", "USERS"));
    }

    @Override
    public void handleConsoleCommand(ConsoleCommandSender sender, Map<String, List<String>> args) {

        if (args.containsKey("CONFIG")) {
            MyJFQL.getInstance().reloadConfig();
            sender.sendInfo("Loaded the 'config.json'.");
            return;
        }

        if (args.containsKey("DATABASES")) {
            MyJFQL.getInstance().reloadDatabases();
            sender.sendInfo("Reloaded all databases.");
            return;
        }

        if (args.containsKey("USERS")) {
            MyJFQL.getInstance().reloadUsers();
            sender.sendInfo("Reloaded all users.");
            return;
        }

        if (args.containsKey("SERVER")) {
            MyJFQL.getInstance().restartServer();
            return;
        }

        sender.sendSyntax();
    }
}
