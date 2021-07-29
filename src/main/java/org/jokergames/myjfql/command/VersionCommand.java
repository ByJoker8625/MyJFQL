package org.jokergames.myjfql.command;

import org.jokergames.myjfql.core.MyJFQL;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class VersionCommand extends ConsoleCommand {

    public VersionCommand() {
        super("version", Arrays.asList("COMMAND", "DISPLAY", "UPDATE"));
    }

    @Override
    public void handleConsoleCommand(final ConsoleCommandSender sender, final Map<String, List<String>> args) {
        if (args.containsKey("DISPLAY")) {
            sender.sendAnswer(Arrays.asList(MyJFQL.getInstance().getVersion()), new String[]{"Version"});
            return;
        }

        if (args.containsKey("UPDATE")) {
            MyJFQL.getInstance().getDownloader().download();
            return;
        }

        sender.sendSyntax();
    }

}
