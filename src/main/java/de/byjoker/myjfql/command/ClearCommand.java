package de.byjoker.myjfql.command;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@CommandHandler
public class ClearCommand extends ConsoleCommand {

    public ClearCommand() {
        super("clear", Collections.singletonList("COMMAND"));
    }

    @Override
    public void handleConsoleCommand(ConsoleCommandSender sender, Map<String, List<String>> args) {
        sender.getConsole().clear();
    }

}
