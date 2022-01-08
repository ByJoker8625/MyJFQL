package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.core.lang.ConditionFormatter;
import de.byjoker.myjfql.database.*;
import de.byjoker.myjfql.user.session.Session;

import java.util.*;

@CommandHandler
public class InsertCommand extends Command {

    public InsertCommand() {
        super("insert", Arrays.asList("COMMAND", "INTO", "KEY", "VALUE", "PRIMARY-KEY", "WHERE"));
    }

    @Override
    public void handleCommand(CommandSender sender, Map<String, List<String>> args) {
        final DatabaseService databaseService = MyJFQL.getInstance().getDatabaseService();
        final Session session = sender.getSession();

        if (session == null) {
            sender.sendError("Session of this user is invalid!");
            return;
        }

        final Database database = session.getDatabase(MyJFQL.getInstance().getDatabaseService());

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

            if (!sender.allowed(database.getId(), DatabaseAction.READ_WRITE)) {
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

            final Collection<String> tableStructure = table.getStructure();
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
                    columns = ConditionFormatter.getRequiredColumns(table, args.get("WHERE"));
                } catch (Exception ex) {
                    sender.sendError(ex);
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
