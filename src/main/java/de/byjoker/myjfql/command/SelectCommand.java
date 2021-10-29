package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.Column;
import de.byjoker.myjfql.database.Database;
import de.byjoker.myjfql.database.DatabaseAction;
import de.byjoker.myjfql.database.Table;
import de.byjoker.myjfql.user.session.Session;
import de.byjoker.myjfql.util.ConditionHelper;
import de.byjoker.myjfql.util.Sorter;

import java.util.*;
import java.util.stream.Collectors;

@CommandExecutor
public class SelectCommand extends Command {

    public SelectCommand() {
        super("select", Arrays.asList("COMMAND", "VALUE", "FROM", "WHERE", "LIMIT", "SORT", "ORDER", "PRIMARY-KEY"));
    }

    @Override
    public void handleCommand(CommandSender sender, Map<String, List<String>> args) {
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

        if (args.containsKey("VALUE")
                && args.containsKey("FROM")) {

            final String name = formatString(args.get("FROM"));

            if (name == null) {
                sender.sendError("Undefined name!");
                return;
            }

            if (!database.existsTable(name)) {
                sender.sendError("Table doesn't exists!");
                return;
            }

            final Table table = database.getTable(name);

            if (!sender.allowed(database.getId(), DatabaseAction.READ)) {
                sender.sendForbidden();
                return;
            }

            final List<String> values = new ArrayList<>();
            final List<String> tableStructure = table.getStructure();

            {
                List<String> strings = formatList(args.get("VALUE"));

                if (strings.size() == 0) {
                    sender.sendError("You need to specify the values!");
                    return;
                }

                strings = formatList(strings);

                if (!strings.contains("*")) {
                    for (final String key : strings) {
                        if (!tableStructure.contains(key)) {
                            sender.sendError("Key doesn't exists!");
                            return;
                        }

                        values.add(key);
                    }
                } else {
                    values.addAll(table.getStructure());
                }
            }

            Sorter.Type type = Sorter.Type.CREATION;
            Sorter.Order order = Sorter.Order.ASC;
            String sorter = null;
            int limit = -1;

            if (args.containsKey("LIMIT")) {
                try {
                    limit = formatInteger(args.get("LIMIT"));
                } catch (Exception ex) {
                    sender.sendError("Unknown limit!");
                    return;
                }

                if (limit <= 0) {
                    sender.sendError("Limit is too small!");
                    return;
                }
            }

            if (args.containsKey("SORT")) {
                final String sort = formatString(args.get("SORT"));

                if (sort == null) {
                    sender.sendError("Undefined sort item!");
                    return;
                }

                if (!tableStructure.contains(sort)) {
                    sender.sendError("Sort item doesn't exist!");
                }

                type = Sorter.Type.CUSTOM;
                sorter = sort;
            }

            if (args.containsKey("ORDER")) {
                try {
                    order = Sorter.Order.valueOf(formatString(args.get("ORDER")));
                } catch (Exception ex) {
                    sender.sendError("Unknown sort order!");
                    return;
                }

                if (type != Sorter.Type.CUSTOM)
                    type = Sorter.Type.CUSTOM;

                if (sorter == null)
                    sorter = table.getPrimary();
            }

            if (args.containsKey("PRIMARY-KEY")) {
                final String primaryKey = formatString(args.get("PRIMARY-KEY"));

                if (primaryKey == null) {
                    sender.sendError("Undefined primary-key!");
                    return;
                }

                final Column column = table.getColumn(primaryKey);

                if (column == null) {
                    sender.sendError("Column was not found!");
                    return;
                }

                sender.sendResult(Collections.singletonList(column), values);
            } else if (args.containsKey("WHERE")) {
                List<Column> columns;

                try {
                    columns = ConditionHelper.getRequiredColumns(table, args.get("WHERE"), type, sorter, order);
                } catch (Exception ex) {
                    sender.sendError("Unknown statement error!");
                    return;
                }

                if (columns == null) {
                    sender.sendError("Unknown statement error!");
                    return;
                }

                if (limit == -1)
                    sender.sendResult(columns, values);
                else
                    sender.sendResult(columns.stream().limit(limit).collect(Collectors.toList()), values);
            } else {
                final List<Column> columns = table.getColumns(type, order, sorter);

                if (columns.size() == 0) {
                    sender.sendResult(new ArrayList<Column>(), values);
                    return;
                }

                if (limit != -1) {
                    sender.sendResult(columns.stream().limit(limit).collect(Collectors.toList()), values);
                    return;
                }

                sender.sendResult(columns, values);
            }

            return;
        }

        sender.sendSyntax();
    }
}
