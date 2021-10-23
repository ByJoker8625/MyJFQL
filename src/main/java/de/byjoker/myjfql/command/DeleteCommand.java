package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.DBSession;
import de.byjoker.myjfql.database.Database;
import de.byjoker.myjfql.database.DatabaseService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@CommandExecutor
public class DeleteCommand extends Command {

    public DeleteCommand() {
        super("delete", Arrays.asList("COMMAND", "DATABASE", "TABLE", "FROM"));
    }

    @Override
    public void handleCommand(CommandSender sender, Map<String, List<String>> args) {
        final DatabaseService databaseService = MyJFQL.getInstance().getDatabaseService();
        final DBSession session = MyJFQL.getInstance().getDBSession();

        if (args.containsKey("TABLE")) {
            final String name = formatString(args.get("TABLE"));
            String databaseName = session.get(sender.getName());

            if (name == null) {
                sender.sendError("No database is in use for this user!");
                return;
            }

            if (args.containsKey("FROM")) {
                final String string = formatString(args.get("FROM"));

                if (string == null) {
                    sender.sendError("Undefined database!");
                    return;
                }

                if ((!sender.hasPermission("use.database." + string)
                        && !sender.hasPermission("use.database.*"))
                        || sender.hasPermission("-use.database." + string)
                        || sender.hasPermission("-use.database.*")) {
                    sender.sendForbidden();
                    return;
                }

                databaseName = string;
            }

            if (!databaseService.existsDatabase(databaseName)) {
                sender.sendError("Database doesn't exists!");
                return;
            }

            if ((sender.hasPermission("-use.table." + name + "." + databaseName) || sender.hasPermission("-use.table.*." + databaseName))
                    || (!sender.hasPermission("use.table." + name + "." + databaseName) && !sender.hasPermission("use.table.*." + databaseName))) {
                sender.sendForbidden();
                return;
            }

            final Database database = databaseService.getDatabase(databaseName);

            if (!database.existsTable(name)) {
                sender.sendError("Table doesn't exists!");
                return;
            }

            sender.sendSuccess();

            database.deleteTable(name);
            databaseService.saveDatabase(database);
            return;
        }

        if (args.containsKey("DATABASE")) {
            if (sender instanceof RemoteCommandSender) {
                sender.sendForbidden();
                return;
            }

            final String name = formatString(args.get("DATABASE"));

            if (name == null) {
                sender.sendError("Undefined database!");
                return;
            }

            if (!databaseService.existsDatabase(name)) {
                sender.sendError("Database doesn't exists!");
                return;
            }

            databaseService.deleteDatabase(name);
            sender.sendSuccess();
            return;
        }

        sender.sendSyntax();
    }

}
