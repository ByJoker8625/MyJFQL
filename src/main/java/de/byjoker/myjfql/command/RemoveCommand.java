package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.Column;
import de.byjoker.myjfql.database.Database;
import de.byjoker.myjfql.database.DatabaseService;
import de.byjoker.myjfql.database.Table;
import de.byjoker.myjfql.util.ConditionHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@CommandExecutor
public class RemoveCommand extends Command {

    public RemoveCommand() {
        super("remove", Arrays.asList("COMMAND", "COLUMN", "FROM", "WHERE"));
    }

    @Override
    public void handleCommand(CommandSender sender, Map<String, List<String>> args) {
        final DatabaseService databaseService = MyJFQL.getInstance().getDatabaseService();
        final Database database = MyJFQL.getInstance().getDBSession().getDirectlyDatabase(sender.getName());

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

            final String databaseName = database.getName();

            if ((sender.hasPermission("-use.table." + name + "." + databaseName) || sender.hasPermission("-use.table.*." + databaseName))
                    || (!sender.hasPermission("use.table." + name + "." + databaseName) && !sender.hasPermission("use.table.*." + databaseName))) {
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
                    columns = ConditionHelper.getRequiredColumns(table, args.get("WHERE"));
                } catch (Exception ex) {
                    sender.sendError("Unknown statement error!");
                    return;
                }

                if (columns == null) {
                    sender.sendError("Unknown statement error!");
                    return;
                }

                final String primary = table.getPrimary();
                columns.stream().map(col -> col.getContent(primary).toString()).forEach(table::removeColumn);

                sender.sendSuccess();

                database.saveTable(table);
                databaseService.saveDatabase(database);
            } else {
                if (!column.equals("*")) {
                    table.removeColumn(column);
                } else {
                    table.setColumns(new ArrayList<>());
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
