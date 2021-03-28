package org.jokergames.myjfql.command;

import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.database.DBSession;
import org.jokergames.myjfql.database.DatabaseService;

import java.util.List;
import java.util.Map;

public class UseCommand extends Command {

    public UseCommand() {
        super("use", List.of("COMMAND", "DATABASE"));
    }

    @Override
    public void handle(final CommandSender sender, final Map<String, List<String>> args) {
        final DatabaseService databaseService = MyJFQL.getInstance().getDatabaseService();
        final DBSession session = MyJFQL.getInstance().getDBSession();

        if (args.containsKey("DATABASE")) {
            if (sender.isStaticDatabase()) {
                sender.sendError("You can't change your database!");
                return;
            }

            final String name = formatString(args.get("DATABASE"));

            if (name == null) {
                sender.sendError("Unknown database!");
                return;
            }

            if (!databaseService.isCreated(name)) {
                sender.sendError("Database doesn't exists!");
                return;
            }

            if (!sender.hasPermission("use.database." + name)
                    && !sender.hasPermission("use.database.*")) {
                sender.sendForbidden();
                return;
            }

            session.put(sender.getName(), name);
            sender.sendSuccess();
            return;
        }

        sender.sendSyntax();
    }
}
