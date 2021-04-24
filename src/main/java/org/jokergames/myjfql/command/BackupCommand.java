package org.jokergames.myjfql.command;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BackupCommand extends Command {

    public BackupCommand() {
        super("backup", Collections.singletonList("COMMAND"));
    }

    @Override
    public void handle(CommandSender sender, Map<String, List<String>> args) {
        if (sender instanceof RemoteCommandSender) {
            sender.sendForbidden();
            return;
        }

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
    }


}
