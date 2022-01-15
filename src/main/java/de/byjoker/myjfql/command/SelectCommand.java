package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.*;
import de.byjoker.myjfql.lang.ColumnComparator;
import de.byjoker.myjfql.lang.ColumnFilter;
import de.byjoker.myjfql.lang.SortingOrder;
import de.byjoker.myjfql.server.session.Session;

import java.util.*;
import java.util.stream.Collectors;

@CommandHandler
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
            final Collection<String> tableStructure = table.getStructure();

            {
                List<String> strings = formatList(args.get("VALUE"));

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

            SortingOrder order = null;
            String sortedBy = null;
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

                sortedBy = sort;
            }

            if (args.containsKey("ORDER")) {
                try {
                    order = SortingOrder.valueOf(formatString(args.get("ORDER")));
                } catch (Exception ex) {
                    sender.sendError("Unknown sort order!");
                    return;
                }

                if (sortedBy == null)
                    sortedBy = table.getPrimary();
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
                    columns = ColumnFilter.filter(table, args.get("WHERE"), new ColumnComparator(sortedBy), order);
                } catch (Exception ex) {
                    sender.sendError(ex);
                    return;
                }

                if (columns == null) {
                    sender.sendError("Unknown statement error!");
                    return;
                }

                if (limit != -1) {
                    sender.sendResult(columns.stream().limit(limit).collect(Collectors.toList()), values);
                    return;
                }

                sender.sendResult(columns, values);
            } else {
                final Collection<Column> columns = sortedBy == null ? table.getColumns() : table.getColumns(new ColumnComparator(sortedBy), order);

                if (columns.size() == 0) {
                    sender.sendResult(new ArrayList<SimpleColumn>(), values);
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
