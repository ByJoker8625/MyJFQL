package de.byjoker.myjfql.command;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@CommandHandler
public class ClearCommand extends ConsoleCommand {

    public ClearCommand() {
        super("clear", Collections.singletonList("COMMAND"));
    }

    @Override
    public void executeAsConsole(ConsoleCommandSender sender, @NotNull Map<String, ? extends List<String>> args) {
        sender.getConsole().clear();
    }

}
