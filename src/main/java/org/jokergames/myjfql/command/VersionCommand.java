package org.jokergames.myjfql.command;

import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.util.Updater;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class VersionCommand extends ConsoleCommand {

    public VersionCommand() {
        super("version", Arrays.asList("COMMAND", "DISPLAY", "UPDATE"));
    }

    @Override
    public void handleConsoleCommand(final ConsoleCommandSender sender, final Map<String, List<String>> args) {
        final Updater updater = MyJFQL.getInstance().getUpdater();
        final Updater.Downloader downloader = MyJFQL.getInstance().getDownloader();

        if (args.containsKey("DISPLAY")) {
            sender.sendAnswer(Arrays.asList(MyJFQL.getInstance().getVersion()), new String[]{"Version"});
            return;
        }

        if (args.containsKey("UPDATE")) {
            final List<String> update = args.get("UPDATE");

            if (update.size() == 0)
                downloader.downloadLatestVersion();
            else {
                String version = formatString(update);

                if (version == null) {
                    sender.sendError("Undefined version!");
                    return;
                }

                if (!updater.getVersions().contains(version)) {
                    sender.sendError("Unknown version!");
                    return;
                }

                downloader.downloadByVersion(version);
                return;
            }

            return;
        }

        sender.sendSyntax();
    }

}
