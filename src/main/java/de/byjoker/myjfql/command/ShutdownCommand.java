package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@CommandHandler
public class ShutdownCommand extends ConsoleCommand {

    public ShutdownCommand() {
        super("shutdown", Collections.singletonList("COMMAND"));
    }

    @Override
    public void executeAsConsole(ConsoleCommandSender sender, @NotNull Map<String, ? extends List<String>> args) {
        MyJFQL.getInstance().shutdown();
    }

}
