package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.*;
import de.byjoker.myjfql.lang.ColumnFilter;
import de.byjoker.myjfql.server.session.Session;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@CommandHandler
public class RemoveCommand extends Command {

    public RemoveCommand() {
        super("remove", Arrays.asList("COMMAND", "COLUMN", "FROM", "WHERE"));
    }

    @Override
    public void handleCommand(CommandSender sender, Map<String, List<String>> args) {
        final DatabaseService databaseService = MyJFQL.getInstance().getDatabaseService();
        final Session session = sender.getSession();

        if (session == null) {
            sender.sendError("Session of this user is invalid!");
            return;
        }

        final Database database = session.getDatabase(databaseService);

        if (database == null) {
            sender.sendError("No database is in use for this user!");
            return;
        }

        if (args.containsKey("FROM")
                && args.containsKey("COLUMN")) {
            final String name = formatString(args.get("FROM"));
            final String column = formatString(args.get("COLUMN"));

            if (name == null) {
                sender.sendError("Undefined table!");
                return;
            }

            if (column == null) {
                sender.sendError("Undefined column!");
                return;
            }

            if (!database.existsTable(name)) {
                sender.sendError("Table doesn't exists!");
                return;
            }

            if (!sender.allowed(database.getId(), DatabaseAction.READ_WRITE)) {
                sender.sendForbidden();
                return;
            }

            final Table table = database.getTable(name);

            if (!column.equals("*")
                    && table.getColumn(column) == null) {
                sender.sendError("Column doesn't exist!");
                return;
            }

            if (args.containsKey("WHERE")) {
                List<Column> columns;

                try {
                    columns = ColumnFilter.filter(table, args.get("WHERE"));
                } catch (Exception ex) {
                    sender.sendError(ex);
                    return;
                }

                if (columns == null) {
                    sender.sendError("Unknown statement error!");
                    return;
                }

                final String primary = table.getPrimary();
                columns.stream().map(col -> col.getStringifyItem(primary)).forEach(table::removeColumn);

                sender.sendSuccess();

                database.saveTable(table);
                databaseService.saveDatabase(database);
            } else {
                if (!column.equals("*")) {
                    table.removeColumn(column);
                } else {
                    table.clear();
                }

                sender.sendSuccess();

                database.saveTable(table);
                databaseService.saveDatabase(database);
            }

            return;
        }

        sender.sendSyntax();
    }
}
