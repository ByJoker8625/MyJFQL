package org.jokergames.myjfql.command;

import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.database.Column;
import org.jokergames.myjfql.database.Database;
import org.jokergames.myjfql.database.DatabaseService;
import org.jokergames.myjfql.database.Table;
import org.jokergames.myjfql.util.ConditionHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsertCommand extends Command {

    public InsertCommand() {
        super("insert", Arrays.asList("COMMAND", "INTO", "KEY", "VALUE", "PRIMARY-KEY", "WHERE"));
    }

    @Override
    public void handleCommand(final CommandSender sender, final Map<String, List<String>> args) {
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
                database.addTable(table);
                databaseService.saveDataBase(database);
            } else if (args.containsKey("WHERE")) {
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

                for (final Column column : columns) {
                    column.getContent().putAll(content);
                    table.addColumn(column);
                }

                sender.sendSuccess();

                database.addTable(table);
                databaseService.saveDataBase(database);
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
                database.addTable(table);
                databaseService.saveDataBase(database);
            }

            return;
        }

        sender.sendSyntax();
    }

}
