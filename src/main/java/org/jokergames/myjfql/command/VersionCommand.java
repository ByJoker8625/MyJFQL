package org.jokergames.myjfql.command;

import org.jokergames.myjfql.core.MyJFQL;

import java.util.List;
import java.util.Map;

public class VersionCommand extends Command {

    public VersionCommand() {
        super("version", List.of("COMMAND", "DISPLAY", "UPDATE"));
    }

    @Override
    public void handle(final CommandSender sender, final Map<String, List<String>> args) {
        if (sender instanceof RemoteCommandSender) {
            sender.sendForbidden();
            return;
        }

        if (args.containsKey("DISPLAY")) {
            sender.sendAnswer(List.of(MyJFQL.getInstance().getVersion()), new String[]{"Version"});
            return;
        }

        if (args.containsKey("UPDATE")) {
            MyJFQL.getInstance().getDownloader().download();
            return;
        }

        sender.sendSyntax();
    }

}
