package org.jokergames.myjfql.command;

import org.jokergames.myjfql.core.MyJFQL;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class LastUpdateCommand extends Command {

    public LastUpdateCommand() {
        super("lastupdate", Collections.singletonList("COMMAND"));
    }

    @Override
    public void handle(CommandSender sender, Map<String, List<String>> args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendForbidden();
            return;
        }

        final long update = MyJFQL.getInstance().getLastUpdate();

        if (update == -1) {
            sender.sendError("No update has been made yet!");
            return;
        }

        sender.sendInfo("Last updated at: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(update)));
    }
}
