package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.Database;
import de.byjoker.myjfql.database.DatabaseService;
import de.byjoker.myjfql.database.Table;
import de.byjoker.myjfql.util.Sorter;

import java.util.*;
import java.util.stream.Collectors;

@CommandExecutor
public class ListCommand extends Command {

    public ListCommand() {
        super("list", Arrays.asList("COMMAND", "TABLES", "DATABASES", "ORDER", "FROM", "LIMIT"));
    }

    @Override
    public void handleCommand(CommandSender sender, Map<String, List<String>> args) {
        final DatabaseService databaseService = MyJFQL.getInstance().getDatabaseService();

        if (args.containsKey("DATABASES")) {
            if (sender.hasPermission("-use.database.*")) {
                sender.sendForbidden();
                return;
            }

            List<String> databases = databaseService.getDatabases().stream().map(Database::getName).filter(db ->
                            !sender.hasPermission("-use.database." + db) && (sender.hasPermission("use.database." + db) || sender.hasPermission("use.database.*")))
                    .collect(Collectors.toList());

            if (args.containsKey("LIMIT")) {
                int limit;

                try {
                    limit = formatInteger(args.get("LIMIT"));
                } catch (Exception ex) {
                    sender.sendError("Unknown or undefined limit!");
                    return;
                }

                if (limit <= 0) {
                    sender.sendError("Limit is too small!");
                    return;
                }

                if (databases.size() > limit) {
                    databases = databases.stream().limit(limit).collect(Collectors.toList());
                }
            }

            if (args.containsKey("ORDER")) {
                Sorter.Order order;

                try {
                    order = Sorter.Order.valueOf(Objects.requireNonNull(formatString(args.get("ORDER"))).toUpperCase());
                } catch (Exception ex) {
                    sender.sendError("Unknown or undefined sort order!");
                    return;
                }

                sender.sendAnswer(Sorter.sortList(databases, order), new String[]{"Database"});
                return;
            }


            sender.sendAnswer(databases, new String[]{"Database"});
            return;
        }

        if (args.containsKey("TABLES")) {
            if (sender.hasPermission("-use.table.*.*") || sender.hasPermission("-use.database.*")) {
                sender.sendForbidden();
                return;
            }

            List<String> tables = new ArrayList<>();

            if (!args.containsKey("FROM")) {
                for (final Database database : databaseService.getDatabases()) {
                    final String databaseName = database.getName();

                    if ((sender.hasPermission("use.database." + databaseName)
                            || sender.hasPermission("use.database.*"))
                            && !sender.hasPermission("-use.database." + databaseName)
                            && !sender.hasPermission("-use.database.*")) {

                        for (final Table table : database.getTables()) {
                            final String tableName = table.getName();

                            if ((sender.hasPermission("use.table.*." + databaseName)
                                    || sender.hasPermission("use.table." + tableName + "." + databaseName))
                                    && !sender.hasPermission("-use.table.*." + databaseName)
                                    && !sender.hasPermission("-use.table." + tableName + "." + databaseName))
                                tables.add(table.getName());
                        }
                    }

                }
            } else {
                final String name = formatString(args.get("FROM"));

                if (!databaseService.existsDatabase(name)) {
                    sender.sendError("Database was not found!");
                    return;
                }

                if ((!sender.hasPermission("use.database." + name)
                        && !sender.hasPermission("use.database.*"))
                        || sender.hasPermission("-use.database." + name)
                        || sender.hasPermission("-use.database.*")) {
                    sender.sendForbidden();
                    return;
                }

                final Database database = databaseService.getDatabase(name);

                for (final Table table : database.getTables()) {
                    final String tableName = table.getName();

                    if ((sender.hasPermission("use.table.*." + name)
                            || sender.hasPermission("use.table." + tableName + "." + name))
                            && !sender.hasPermission("-use.table.*." + name)
                            && !sender.hasPermission("-use.table." + tableName + "." + name))
                        tables.add(table.getName());
                }
            }

            if (args.containsKey("LIMIT")) {
                int limit;

                try {
                    limit = formatInteger(args.get("LIMIT"));
                } catch (Exception ex) {
                    sender.sendError("Unknown or undefined limit!");
                    return;
                }

                if (limit <= 0) {
                    sender.sendError("Limit is too small!");
                    return;
                }

                if (tables.size() > limit) {
                    tables = tables.stream().limit(limit).collect(Collectors.toList());
                }
            }

            if (args.containsKey("ORDER")) {
                Sorter.Order order;

                try {
                    order = Sorter.Order.valueOf(Objects.requireNonNull(formatString(args.get("ORDER"))).toUpperCase());
                } catch (Exception ex) {
                    sender.sendError("Unknown or undefined sort order!");
                    return;
                }

                sender.sendAnswer(Sorter.sortList(tables, order), new String[]{"Table"});
                return;
            }

            sender.sendAnswer(tables, new String[]{"Table"});
            return;
        }

        sender.sendSyntax();
    }

}
