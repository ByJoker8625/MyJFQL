package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.RelationalTableEntry;
import de.byjoker.myjfql.util.Downloader;
import de.byjoker.myjfql.util.ResultType;
import de.byjoker.myjfql.util.Updater;
import org.jetbrains.annotations.NotNull;

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
    public void executeAsConsole(ConsoleCommandSender sender, @NotNull Map<String, ? extends List<String>> args) {
        final Updater updater = MyJFQL.getInstance().getUpdater();
        final Downloader downloader = MyJFQL.getInstance().getDownloader();

        if (args.containsKey("DISPLAY")) {
            sender.sendResult(
                    Collections.singletonList(new RelationalTableEntry().append("version", MyJFQL.getInstance().getVersion())),
                    Collections.singletonList("version"), ResultType.RELATIONAL
            );
            return;
        }

        if (args.containsKey("UPDATE")) {
            final List<String> update = args.get("UPDATE");

            if (update.size() == 0) {
                MyJFQL.getInstance().getNetworkService().shutdown();
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

                MyJFQL.getInstance().getNetworkService().shutdown();
                downloader.downloadByVersion(version);
                return;
            }

            return;
        }

        sender.sendSyntax();
    }

}
