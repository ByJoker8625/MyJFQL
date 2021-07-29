package org.jokergames.myjfql.command;

import org.jokergames.myjfql.core.MyJFQL;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ReloadCommand extends ConsoleCommand {

    public ReloadCommand() {
        super("reload", Arrays.asList("COMMAND", "CONFIG", "DATABASES", "SERVER", "USERS"));
    }

    @Override
    public void handleConsoleCommand(final ConsoleCommandSender sender, final Map<String, List<String>> args) {

        if (args.containsKey("CONFIG")) {
            MyJFQL.getInstance().reloadConfig();
            sender.sendInfo("Successfully loaded the 'config.json'.");
            return;
        }

        if (args.containsKey("DATABASES")) {
            MyJFQL.getInstance().reloadDatabases();
            sender.sendInfo("Successfully reloaded all databases.");
            return;
        }

        if (args.containsKey("USERS")) {
            MyJFQL.getInstance().reloadUsers();
            sender.sendInfo("Successfully reloaded all users.");
            return;
        }

        if (args.containsKey("SERVER")) {
            MyJFQL.getInstance().restartServer();
            return;
        }

        sender.sendSyntax();
    }
}
