package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.DBSession;
import de.byjoker.myjfql.database.Database;
import de.byjoker.myjfql.database.DatabaseService;
import de.byjoker.myjfql.database.Table;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@CommandExecutor
public class CreateCommand extends Command {

    public CreateCommand() {
        super("create", Arrays.asList("COMMAND", "DATABASE", "TABLE", "STRUCTURE", "PRIMARY-KEY", "INTO"));
    }

    @Override
    public void handleCommand(CommandSender sender, Map<String, List<String>> args) {
        final DatabaseService databaseService = MyJFQL.getInstance().getDatabaseService();
        final DBSession session = MyJFQL.getInstance().getDBSession();

        if (args.containsKey("TABLE") && args.containsKey("STRUCTURE")) {
            final String name = formatString(args.get("TABLE"));
            final List<String> structure = formatList(args.get("STRUCTURE"));

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

            if (databaseName == null) {
                sender.sendError("No database is in use for this user!");
                return;
            }

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

            if (name.contains("%") || name.contains("#") || name.contains("'")) {
                sender.sendError("Unauthorized characters in the name!");
                return;
            }

            if (database.existsTable(name)) {
                sender.sendError("Table already exists!");
                return;
            }

            sender.sendSuccess();

            database.createTable(new Table(name, structure, primaryKey));
            databaseService.createDatabase(database);
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

            if (name.contains("%") || name.contains("#") || name.contains("'")) {
                sender.sendError("Unauthorized characters in the name!");
                return;
            }

            if (databaseService.existsDatabase(name)) {
                sender.sendError("Database already exists!");
                return;
            }

            databaseService.createDatabase(new Database(name));
            sender.sendSuccess();
            return;
        }

        sender.sendSyntax();
    }

}
