package org.jokergames.myjfql.command;

import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.database.Column;
import org.jokergames.myjfql.database.Database;
import org.jokergames.myjfql.database.DatabaseService;
import org.jokergames.myjfql.database.Table;
import org.jokergames.myjfql.util.ConditionHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RemoveCommand extends Command {

    public RemoveCommand() {
        super("remove", Arrays.asList("COMMAND", "COLUMN", "FROM", "WHERE"));
    }

    @Override
    public void handleCommand(final CommandSender sender, final Map<String, List<String>> args) {
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

            if (!database.isCreated(name)) {
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
                List<Column> columns = null;

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

                database.addTable(table);
                databaseService.saveDataBase(database);
            } else {
                if (!column.equals("*")) {
                    table.removeColumn(column);
                } else {
                    table.setColumns(new ArrayList<>());
                }

                sender.sendSuccess();

                database.addTable(table);
                databaseService.saveDataBase(database);
            }

            return;
        }

        sender.sendSyntax();
    }
}
