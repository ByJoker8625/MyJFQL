package org.jokergames.myjfql.command;

import org.jokergames.myjfql.core.MyJFQL;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RefreshCommand extends ConsoleCommand {

    public RefreshCommand() {
        super("refresh", Arrays.asList("COMMAND", "STATUS", "NOW"));
    }

    @Override
    public void handleConsoleCommand(final ConsoleCommandSender sender, final Map<String, List<String>> args) {
        if (args.containsKey("STATUS")) {
            final long time = MyJFQL.getInstance().getLastRefresh();

            if (time == -1) {
                sender.sendError("No refresh has been made yet!");
                return;
            }

            sender.sendAnswer(Arrays.asList(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time))), new String[]{"Last update"});
            return;
        }

        if (args.containsKey("NOW")) {
            MyJFQL.getInstance().refresh();
            sender.sendInfo("Users and databases were refreshed.");
            return;
        }

        sender.sendSyntax();
    }

}
