package org.jokergames.myjfql.command;

import org.jokergames.myjfql.core.MyJFQL;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ShutdownCommand extends ConsoleCommand {

    public ShutdownCommand() {
        super("shutdown", Collections.singletonList("COMMAND"));
    }

    @Override
    public void handleConsoleCommand(final ConsoleCommandSender sender, final Map<String, List<String>> args) {
        MyJFQL.getInstance().shutdown();
    }

}
