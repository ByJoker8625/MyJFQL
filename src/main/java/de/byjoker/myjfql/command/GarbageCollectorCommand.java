package de.byjoker.myjfql.command;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@CommandExecutor
public class GarbageCollectorCommand extends ConsoleCommand {

    public GarbageCollectorCommand() {
        super("collector", Arrays.asList("COMMAND", "LOAD", "UNLOAD", "TIMER", "OVERVIEW"));
    }

    @Override
    public void handleConsoleCommand(ConsoleCommandSender sender, Map<String, List<String>> args) {

    }

}
