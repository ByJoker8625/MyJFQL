package org.jokergames.myjfql.command;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ClearCommand extends ConsoleCommand {

    public ClearCommand() {
        super("clear", Collections.singletonList("COMMAND"));
    }

    @Override
    public void handleConsoleCommand(final ConsoleCommandSender sender, final Map<String, List<String>> args) {
        sender.getConsole().clear();
    }

}
