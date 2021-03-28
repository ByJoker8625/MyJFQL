package org.jokergames.myjfql.command;

import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.database.DBSession;
import org.jokergames.myjfql.database.Database;
import org.jokergames.myjfql.database.DatabaseService;
import org.jokergames.myjfql.database.Table;

import java.util.List;
import java.util.Map;

public class CreateCommand extends Command {

    public CreateCommand() {
        super("create", List.of("COMMAND", "DATABASE", "TABLE", "STRUCTURE", "PRIMARY-KEY", "INTO"));
    }

    @Override
    public void handle(final CommandSender sender, final Map<String, List<String>> args) {
        final DatabaseService databaseService = MyJFQL.getInstance().getDatabaseService();
        final DBSession session = MyJFQL.getInstance().getDBSession();

        if (args.containsKey("TABLE") && args.containsKey("STRUCTURE")) {
            final String name = formatString(args.get("TABLE"));
            final List<String> structure = args.get("STRUCTURE");

            if (name == null) {
                sender.sendError("Unknown table!");
                return;
            }

            if (structure.size() == 0) {
                sender.sendError("Unknown structure!");
                return;
            }

            String databaseName = session.get(sender.getName());
            String primaryKey = structure.get(0);

            if (name == null) {
                sender.sendError("Name can't be null!");
                return;
            }

            if (args.containsKey("PRIMARY-KEY")) {
                final String string = formatString(args.get("PRIMARY-KEY"));

                if (string == null) {
                    sender.sendError("Key can't be null!");
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
                    sender.sendError("Database can't be null!");
                    return;
                }

                databaseName = string;
            }

            if (!databaseService.isCreated(databaseName)) {
                sender.sendError("Unknown database!");
                return;
            }

            if (!sender.hasPermission("use.table." + name + "." + databaseName)
                    && !sender.hasPermission("use.table.*." + databaseName)) {
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
                sender.sendError("Unknown database!");
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
