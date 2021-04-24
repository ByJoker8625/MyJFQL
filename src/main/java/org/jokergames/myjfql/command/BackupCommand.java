package org.jokergames.myjfql.command;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BackupCommand extends Command {

    public BackupCommand() {
        super("backup", List.of("COMMAND"));
    }

    @Override
    public void handle(CommandSender sender, Map<String, List<String>> args) {
        if (sender instanceof RemoteCommandSender) {
            sender.sendForbidden();
            return;
        }

        final File folder = new File(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()));

        if (!folder.exists())
            folder.mkdir();

        try {
            FileUtils.copyDirectory(new File("database"), folder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sender.sendInfo("Successfully created a backup for all databases!");
    }


}
