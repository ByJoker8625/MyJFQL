package org.jokergames.myjfql.command;

import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.database.DBSession;
import org.jokergames.myjfql.database.Database;
import org.jokergames.myjfql.database.DatabaseService;
import org.jokergames.myjfql.database.Table;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CreateCommand extends Command {

    public CreateCommand() {
        super("create", Arrays.asList("COMMAND", "DATABASE", "TABLE", "STRUCTURE", "PRIMARY-KEY", "INTO"));
    }

    @Override
    public void handleCommand(final CommandSender sender, final Map<String, List<String>> args) {
        final DatabaseService databaseService = MyJFQL.getInstance().getDatabaseService();
        final DBSession session = MyJFQL.getInstance().getDBSession();

        if (args.containsKey("TABLE") && args.containsKey("STRUCTURE")) {
            final String name = formatString(args.get("TABLE"));
            final List<String> structure = args.get("STRUCTURE");

            if (name == null) {
                sender.sendError("Undefined table!");
                return;
            }

            if (structure.size() == 0) {
                sender.sendError("Undefined structure!");
                return;
            }

            String databaseName = session.get(sender.getName());
            String primaryKey = structure.get(0);

            if (args.containsKey("PRIMARY-KEY")) {
                final String string = formatString(args.get("PRIMARY-KEY"));

                if (string == null) {
                    sender.sendError("Undefined key!");
                    return;
                }

                if (!structure.contains(string)) {
                    sender.sendError("Unknown key!");
                    return;
                }

                primaryKey = string;
            }

            if (args.containsKey("INTO")) {
                final String string = formatString(args.get("INTO"));

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
                sender.sendError("Database doesn't exist!");
                return;
            }

            if ((sender.hasPermission("-use.table." + name + "." + databaseName) || sender.hasPermission("-use.table.*." + databaseName))
                    || (!sender.hasPermission("use.table." + name + "." + databaseName) && !sender.hasPermission("use.table.*." + databaseName))) {
                sender.sendForbidden();
                return;
            }

            final Database database = databaseService.getDataBase(databaseName);

            if (database.isCreated(name)) {
                sender.sendError("Table already exists!");
                return;
            }

            sender.sendSuccess();

            database.addTable(new Table(name, structure, primaryKey));
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

            if (databaseService.isCreated(name)) {
                sender.sendError("Database already exists!");
                return;
            }

            databaseService.saveDataBase(new Database(name));
            sender.sendSuccess();
            return;
        }

        sender.sendSyntax();
    }

}
