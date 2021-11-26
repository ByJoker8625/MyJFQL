package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.Database;
import de.byjoker.myjfql.database.DatabaseAction;
import de.byjoker.myjfql.database.DatabaseService;
import de.byjoker.myjfql.database.Table;
import de.byjoker.myjfql.util.Sorter;

import java.util.*;
import java.util.stream.Collectors;

@CommandHandler
public class ListCommand extends Command {

    public ListCommand() {
        super("list", Arrays.asList("COMMAND", "TABLES", "DATABASES", "ORDER", "FROM", "LIMIT"));
    }

    @Override
    public void handleCommand(CommandSender sender, Map<String, List<String>> args) {
        final DatabaseService databaseService = MyJFQL.getInstance().getDatabaseService();

        if (sender.getSession() == null) {
            sender.sendError("Session of this user is invalid!");
            return;
        }

        if (args.containsKey("DATABASES")) {
            List<String> databases = databaseService.getDatabases().stream().filter(database -> sender.allowed(database.getId(), DatabaseAction.READ))
                    .map(Database::getName)
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

                sender.sendResult(Sorter.sortList(databases, order), new String[]{"Database"});
                return;
            }


            sender.sendResult(databases, new String[]{"Database"});
            return;
        }

        if (args.containsKey("TABLES")) {
            List<String> tables = new ArrayList<>();

            if (!args.containsKey("FROM")) {
                List<String> finalTables = tables;
                databaseService.getDatabases().stream().filter(database -> sender.allowed(database.getId(), DatabaseAction.READ)).forEach(database -> {
                    finalTables.addAll(database.getTables().stream().map(Table::getName).collect(Collectors.toList()));
                });
            } else {
                final String identifier = formatString(args.get("FROM"));

                if (!databaseService.existsDatabaseByIdentifier(identifier)) {
                    sender.sendError("Database was not found!");
                    return;
                }

                final Database database = databaseService.getDatabaseByIdentifier(identifier);

                if (!sender.allowed(database.getId(), DatabaseAction.READ)) {
                    sender.sendForbidden();
                    return;
                }

                tables.addAll(database.getTables().stream().map(Table::getName).collect(Collectors.toList()));
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

                sender.sendResult(Sorter.sortList(tables, order), new String[]{"Table"});
                return;
            }

            sender.sendResult(tables, new String[]{"Table"});
            return;
        }

        sender.sendSyntax();
    }

}
