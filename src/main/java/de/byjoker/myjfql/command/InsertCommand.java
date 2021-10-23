package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.Column;
import de.byjoker.myjfql.database.Database;
import de.byjoker.myjfql.database.DatabaseService;
import de.byjoker.myjfql.database.Table;
import de.byjoker.myjfql.util.ConditionHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CommandExecutor
public class InsertCommand extends Command {

    public InsertCommand() {
        super("insert", Arrays.asList("COMMAND", "INTO", "KEY", "VALUE", "PRIMARY-KEY", "WHERE"));
    }

    @Override
    public void handleCommand(CommandSender sender, Map<String, List<String>> args) {
        final DatabaseService databaseService = MyJFQL.getInstance().getDatabaseService();
        final Database database = MyJFQL.getInstance().getDBSession().getDirectlyDatabase(sender.getName());

        if (database == null) {
            sender.sendError("No database is in use for this user!");
            return;
        }

        if (args.containsKey("INTO")
                && args.containsKey("KEY")
                && args.containsKey("VALUE")) {
            final String name = formatString(args.get("INTO"));

            if (name == null) {
                sender.sendError("Undefined table!");
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

            final List<String> keys = formatList(args.get("KEY"));
            final List<String> values = formatList(args.get("VALUE"));

            if (keys.size() != values.size()) {
                sender.sendError("Enter suitable keys and values!");
                return;
            }

            final Table table = database.getTable(name);

            final List<String> tableStructure = table.getStructure();
            final String primary = table.getPrimary();

            final Map<String, String> content = new HashMap<>();

            for (int i = 0; i < keys.size(); i++) {
                final String key = keys.get(i);

                if (!tableStructure.contains(key)) {
                    sender.sendError("Key doesn't exists!");
                    return;
                }

                content.put(key, values.get(i));
            }

            if (args.containsKey("PRIMARY-KEY")) {
                final String primaryKey = formatString(args.get("PRIMARY-KEY"));

                if (primaryKey == null) {
                    sender.sendError("Undefined primary-key!");
                    return;
                }

                Column column = table.getColumn(primaryKey);

                if (column == null) {
                    column = new Column();
                }

                column.getContent().putAll(content);

                if (column.getContent(primary) == null) {
                    column.putContent(primary, primaryKey);
                }

                sender.sendSuccess();

                table.addColumn(column);
                database.saveTable(table);
                databaseService.saveDatabase(database);
            } else if (args.containsKey("WHERE")) {
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

                for (final Column column : columns) {
                    column.getContent().putAll(content);
                    table.addColumn(column);
                }

                sender.sendSuccess();

                database.saveTable(table);
                databaseService.saveDatabase(database);
            } else {
                if (!content.containsKey(primary)) {
                    sender.sendError("No primary-key found!");
                    return;
                }

                Column column = table.getColumn(content.get(primary));

                if (column == null) {
                    column = new Column();
                }

                column.getContent().putAll(content);

                sender.sendSuccess();

                table.addColumn(column);
                database.saveTable(table);
                databaseService.saveDatabase(database);
            }

            return;
        }

        sender.sendSyntax();
    }

}
