package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.util.Downloader;
import de.byjoker.myjfql.util.Updater;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@CommandHandler
public class VersionCommand extends ConsoleCommand {

    public VersionCommand() {
        super("version", Arrays.asList("COMMAND", "DISPLAY", "UPDATE"));
    }

    @Override
    public void handleConsoleCommand(ConsoleCommandSender sender, Map<String, List<String>> args) {
        final Updater updater = MyJFQL.getInstance().getUpdater();
        final Downloader downloader = MyJFQL.getInstance().getDownloader();

        if (args.containsKey("DISPLAY")) {
            sender.sendResult(Collections.singletonList(MyJFQL.getInstance().getVersion()), new String[]{"version"});
            return;
        }

        if (args.containsKey("UPDATE")) {
            final List<String> update = args.get("UPDATE");

            if (update.size() == 0) {
                MyJFQL.getInstance().getServer().shutdown();
                downloader.downloadLatestVersion();
            } else {
                String version = formatString(update);

                if (version == null) {
                    sender.sendError("Undefined version!");
                    return;
                }

                if (!updater.getVersions().contains(version)) {
                    sender.sendError("Unknown version!");
                    return;
                }

                MyJFQL.getInstance().getServer().shutdown();
                downloader.downloadByVersion(version);
                return;
            }

            return;
        }

        sender.sendSyntax();
    }

}
