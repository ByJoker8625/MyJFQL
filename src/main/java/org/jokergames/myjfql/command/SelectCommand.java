package org.jokergames.myjfql.command;

import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.database.Column;
import org.jokergames.myjfql.database.Database;
import org.jokergames.myjfql.database.Table;
import org.jokergames.myjfql.util.ConditionHelper;
import org.jokergames.myjfql.util.Sorter;

import java.util.*;
import java.util.stream.Collectors;

public class SelectCommand extends Command {

    public SelectCommand() {
        super("select", Arrays.asList("COMMAND", "VALUE", "FROM", "WHERE", "LIMIT", "SORT", "ORDER", "PRIMARY-KEY"));
    }

    @Override
    public void handleCommand(final CommandSender sender, final Map<String, List<String>> args) {
        final Database database = MyJFQL.getInstance().getDBSession().getDirectlyDatabase(sender.getName());

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

            if (!database.isCreated(name)) {
                sender.sendError("Table doesn't exists!");
                return;
            }

            final Table table = database.getTable(name);
            final String databaseName = database.getName();

            if ((sender.hasPermission("-use.table." + name + "." + databaseName) || sender.hasPermission("-use.table.*." + databaseName))
                    || (!sender.hasPermission("use.table." + name + "." + databaseName) && !sender.hasPermission("use.table.*." + databaseName))) {
                sender.sendForbidden();
                return;
            }

            final List<String> values = new ArrayList<>();
            final List<String> tableStructure = table.getStructure();

            {
                List<String> strings = args.get("VALUE");

                if (strings.size() == 0) {
                    sender.sendError("You need to specify the values!");
                    return;
                }

                strings = formatList(strings);

                if (!strings.contains("*")) {
                    for (String key : strings) {
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

                column.getContent().keySet().stream().filter(key -> !values.contains(key)).forEach(key -> column.getContent().remove(key));
                sender.sendAnswer(Collections.singletonList(column), values);
            } else if (args.containsKey("WHERE")) {
                List<Column> columns = null;

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
                    sender.sendAnswer(columns, values);
                else
                    sender.sendAnswer(columns.stream().limit(limit).collect(Collectors.toList()), values);
            } else {
                final List<Column> columns = table.getColumns(type, order, sorter);

                if (columns.size() == 0) {
                    sender.sendAnswer(new ArrayList<Column>(), values);
                    return;
                }

                if (limit != -1) {
                    sender.sendAnswer(columns.stream().limit(limit).collect(Collectors.toList()), values);
                    return;
                }

                sender.sendAnswer(columns, values);
            }

            return;
        }

        sender.sendSyntax();
    }
}
