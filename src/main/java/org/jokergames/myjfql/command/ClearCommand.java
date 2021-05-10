package org.jokergames.myjfql.command;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ClearCommand extends Command {

    public ClearCommand() {
        super("clear", Collections.singletonList("COMMAND"));
    }

    @Override
    public void handle(final CommandSender sender, final Map<String, List<String>> args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendForbidden();
            return;
        }

        ((ConsoleCommandSender) sender).getConsole().clear();
    }

}
