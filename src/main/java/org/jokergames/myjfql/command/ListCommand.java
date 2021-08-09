package org.jokergames.myjfql.command;

import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.database.Database;
import org.jokergames.myjfql.database.DatabaseService;
import org.jokergames.myjfql.database.Table;
import org.jokergames.myjfql.user.User;
import org.jokergames.myjfql.user.UserService;
import org.jokergames.myjfql.util.Sorter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListCommand extends Command {

    public ListCommand() {
        super("list", Arrays.asList("COMMAND", "TABLES", "DATABASES", "USERS", "ORDER", "FROM", "LIMIT"));
    }

    @Override
    public void handleCommand(final CommandSender sender, final Map<String, List<String>> args) {
        final DatabaseService databaseService = MyJFQL.getInstance().getDatabaseService();
        final UserService userService = MyJFQL.getInstance().getUserService();

        if (args.containsKey("DATABASES")) {
            if (sender.hasPermission("-use.database.*")) {
                sender.sendForbidden();
                return;
            }

            List<String> databases = databaseService.getDataBases().stream().map(Database::getName).filter(db ->
                    !sender.hasPermission("-use.database." + db) && (sender.hasPermission("use.database." + db) || sender.hasPermission("use.database.*"))
            ).collect(Collectors.toList());

            if (args.containsKey("LIMIT")) {
                int limit = -1;

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

                if (databases.size() > limit) {
                    databases = databases.stream().limit(limit).collect(Collectors.toList());
                }
            }

            if (args.containsKey("ORDER")) {
                Sorter.Order order = null;

                try {
                    order = Sorter.Order.valueOf(formatString(args.get("ORDER")).toUpperCase());
                } catch (Exception ex) {
                    sender.sendError("Unknown sort order!");
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
                for (Database database : databaseService.getDataBases()) {
                    final String databaseName = database.getName();

                    if ((sender.hasPermission("use.database." + databaseName)
                            || sender.hasPermission("use.database.*"))
                            && !sender.hasPermission("-use.database." + databaseName)
                            && !sender.hasPermission("-use.database.*")) {

                        for (Table table : database.getTables()) {
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

                if (!databaseService.isCreated(name)) {
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

                final Database database = databaseService.getDataBase(name);

                for (Table table : database.getTables()) {
                    final String tableName = table.getName();

                    if ((sender.hasPermission("use.table.*." + name)
                            || sender.hasPermission("use.table." + tableName + "." + name))
                            && !sender.hasPermission("-use.table.*." + name)
                            && !sender.hasPermission("-use.table." + tableName + "." + name))
                        tables.add(table.getName());
                }
            }

            if (args.containsKey("LIMIT")) {
                int limit = -1;

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

                if (tables.size() > limit) {
                    tables = tables.stream().limit(limit).collect(Collectors.toList());
                }
            }

            if (args.containsKey("ORDER")) {
                Sorter.Order order = null;

                try {
                    order = Sorter.Order.valueOf(formatString(args.get("ORDER")).toUpperCase());
                } catch (Exception ex) {
                    sender.sendError("Unknown sort order!");
                    return;
                }

                sender.sendAnswer(Sorter.sortList(tables, order), new String[]{"Table"});
                return;
            }

            sender.sendAnswer(tables, new String[]{"Table"});
            return;
        }

        if (args.containsKey("USERS")) {
            if (sender instanceof RemoteCommandSender) {
                sender.sendForbidden();
                return;
            }

            List<String> users = userService.getUsers().stream().map(User::getName).collect(Collectors.toList());

            if (args.containsKey("LIMIT")) {
                int limit = -1;

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

                if (users.size() > limit) {
                    users = users.stream().limit(limit).collect(Collectors.toList());
                }
            }

            if (args.containsKey("ORDER")) {
                Sorter.Order order = null;

                try {
                    order = Sorter.Order.valueOf(formatString(args.get("ORDER")).toUpperCase());
                } catch (Exception ex) {
                    sender.sendError("Unknown sort order!");
                    return;
                }

                sender.sendAnswer(Sorter.sortList(users, order), new String[]{"User"});
                return;
            }

            sender.sendAnswer(users, new String[]{"User"});
            return;
        }

        sender.sendSyntax();
    }

}
