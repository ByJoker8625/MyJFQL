package org.jokergames.myjfql.command;

import org.apache.commons.io.FileUtils;
import org.jokergames.myjfql.core.MyJFQL;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RefreshCommand extends ConsoleCommand {

    public RefreshCommand() {
        super("refresh", Arrays.asList("COMMAND", "STATUS", "NOW", "AS-BACKUP"));
    }

    @Override
    public void handleConsoleCommand(final ConsoleCommandSender sender, final Map<String, List<String>> args) {
        if (args.containsKey("STATUS")) {
            sender.sendInfo("Last refresh at: " + new Date(MyJFQL.getInstance().getLastRefresh()));
            return;
        }

        if (args.containsKey("AS-BACKUP")) {
            final File folder = new File("backup/" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()));
            final File file = new File("database");

            if (!folder.exists())
                folder.mkdir();

            if (Objects.requireNonNull(file.listFiles()).length == 0) {
                sender.sendInfo("Can't create an backup of empty databases!");
                return;
            }

            try {
                FileUtils.copyDirectory(new File("database"), folder);
            } catch (IOException e) {
                sender.sendError("Backup failed!");
                return;
            }

            sender.sendInfo("Successfully created a backup for all databases!");
            return;
        }

        if (args.containsKey("NOW")) {
            MyJFQL.getInstance().refresh();
            sender.sendInfo("All users and databases were successfully refreshed.");
            return;
        }

        sender.sendSyntax();
    }

}
