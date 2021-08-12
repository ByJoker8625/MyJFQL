package org.jokergames.myjfql.command;

import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.database.DBSession;
import org.jokergames.myjfql.database.Database;
import org.jokergames.myjfql.database.DatabaseService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DeleteCommand extends Command {

    public DeleteCommand() {
        super("delete", Arrays.asList("COMMAND", "DATABASE", "TABLE", "FROM"));
    }

    @Override
    public void handleCommand(final CommandSender sender, final Map<String, List<String>> args) {
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

            if (!databaseService.isCreated(databaseName)) {
                sender.sendError("Database doesn't exists!");
                return;
            }

            if ((sender.hasPermission("-use.table." + name + "." + databaseName) || sender.hasPermission("-use.table.*." + databaseName))
                    || (!sender.hasPermission("use.table." + name + "." + databaseName) && !sender.hasPermission("use.table.*." + databaseName))) {
                sender.sendForbidden();
                return;
            }

            final Database database = databaseService.getDataBase(databaseName);

            if (!database.isCreated(name)) {
                sender.sendError("Table doesn't exists!");
                return;
            }

            sender.sendSuccess();

            database.removeTable(name);
            databaseService.saveDataBase(database);
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

            if (!databaseService.isCreated(name)) {
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
