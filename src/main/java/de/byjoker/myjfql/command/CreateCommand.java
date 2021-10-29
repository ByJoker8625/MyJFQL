package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.Database;
import de.byjoker.myjfql.database.DatabaseAction;
import de.byjoker.myjfql.database.DatabaseService;
import de.byjoker.myjfql.database.Table;
import de.byjoker.myjfql.user.session.Session;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@CommandExecutor
public class CreateCommand extends Command {

    public CreateCommand() {
        super("create", Arrays.asList("COMMAND", "DATABASE", "TABLE", "STRUCTURE", "PRIMARY-KEY"));
    }

    @Override
    public void handleCommand(CommandSender sender, Map<String, List<String>> args) {
        final DatabaseService databaseService = MyJFQL.getInstance().getDatabaseService();
        final Session session = sender.getSession();

        if (session == null) {
            sender.sendError("Session of this user is invalid!");
            return;
        }

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

            final Database database = session.getDatabase(MyJFQL.getInstance().getDatabaseService());

            if (database == null) {
                sender.sendError("No database is in use for this user!");
                return;
            }

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

            if (!sender.allowed(database.getId(), DatabaseAction.READ_WRITE)) {
                sender.sendForbidden();
                return;
            }

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
            databaseService.saveDatabase(database);
            return;
        }

        if (args.containsKey("DATABASE")) {
            if (sender instanceof RestCommandSender) {
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

            if (databaseService.existsDatabaseByName(name)) {
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
