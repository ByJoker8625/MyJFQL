package org.jokergames.myjfql.command;

import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.database.Column;
import org.jokergames.myjfql.database.Database;
import org.jokergames.myjfql.database.DatabaseService;
import org.jokergames.myjfql.database.Table;
import org.jokergames.myjfql.util.ConditionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemoveCommand extends Command {

    public RemoveCommand() {
        super("remove", List.of("COMMAND", "COLUMN", "FROM", "WHERE"));
    }

    @Override
    public void handle(final CommandSender sender, final Map<String, List<String>> args) {
        final DatabaseService databaseService = MyJFQL.getInstance().getDatabaseService();
        final Database database = databaseService.getDataBase(MyJFQL.getInstance().getDBSession().get(sender.getName()));

        if (args.containsKey("FROM")
                && args.containsKey("COLUMN")) {
            final String name = formatString(args.get("FROM"));
            final String column = formatString(args.get("COLUMN"));

            if (name == null) {
                sender.sendError("Unknown table!");
                return;
            }

            if (column == null) {
                sender.sendError("Unknown column!");
                return;
            }

            if (!database.isCreated(name)) {
                sender.sendError("Table doesn't exists!");
                return;
            }

            if (!sender.hasPermission("use.table." + name + "." + database.getName())
                    && !sender.hasPermission("use.table.*." + database.getName())) {
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
