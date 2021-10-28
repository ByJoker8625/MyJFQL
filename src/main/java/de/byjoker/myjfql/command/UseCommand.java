package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.DBSession;
import de.byjoker.myjfql.database.DatabaseService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@CommandExecutor
public class UseCommand extends Command {

    public UseCommand() {
        super("use", Arrays.asList("COMMAND", "DATABASE"));
    }

    @Override
    public void handleCommand(CommandSender sender, Map<String, List<String>> args) {
        final DatabaseService databaseService = MyJFQL.getInstance().getDatabaseService();
        final DBSession session = MyJFQL.getInstance().getDBSession();

        if (args.containsKey("DATABASE")) {
            if (sender.isStaticDatabase()) {
                sender.sendError("You can't change your database!");
                return;
            }

            final String name = formatString(args.get("DATABASE"));

            if (name == null) {
                sender.sendError("Undefined database!");
                return;
            }

            if (!databaseService.existsDatabaseByName(name)) {
                sender.sendError("Database doesn't exists!");
                return;
            }

            if ((!sender.hasPermission("use.database." + name)
                    && !sender.hasPermission("use.database.*"))
                    || sender.hasPermission("-use.database." + name)
                    || sender.hasPermission("-use.database.*")) {
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
