package org.jokergames.myjfql.command;

import org.jokergames.myjfql.command.executor.ConsoleExecutor;
import org.jokergames.myjfql.command.executor.Executor;
import org.jokergames.myjfql.command.executor.RemoteExecutor;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.database.Database;
import org.jokergames.myjfql.database.DatabaseService;
import org.jokergames.myjfql.database.Table;
import org.jokergames.myjfql.script.Script;
import org.jokergames.myjfql.script.ScriptService;
import org.jokergames.myjfql.user.User;
import org.jokergames.myjfql.user.UserService;
import org.jokergames.myjfql.util.Sorter;
import org.jokergames.myjfql.util.TablePrinter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Janick
 */

public class ListCommand extends Command {

    public ListCommand() {
        super("LIST", List.of("COMMAND", "LIMIT", "ORDER", "DATABASES", "TABLES", "USERS", "FROM", "SCRIPTS"));
    }

    @Override
    public boolean handle(Executor executor, Map<String, List<String>> arguments, User user) {
        final DatabaseService dataBaseService = MyJFQL.getInstance().getDatabaseService();
        final UserService userService = MyJFQL.getInstance().getUserService();
        final ScriptService scriptService = MyJFQL.getInstance().getScriptService();
        final Sorter sorter = new Sorter();

        if (executor instanceof RemoteExecutor) {
            RemoteExecutor remote = (RemoteExecutor) executor;

            if (!user.hasPermission("execute.list")) {
                return false;
            }

            Sorter.Order order = Sorter.Order.ASC;
            int limit = -1;

            if (arguments.containsKey("LIMIT")) {
                limit = MyJFQL.getInstance().getFormatter().formatInteger(arguments.get("LIMIT"));

                if (limit <= -1) {
                    remote.sendError("Limit can't be smaller than 0!");
                    return true;
                }
            }

            if (arguments.containsKey("ORDER")) {

                try {
                    order = Sorter.Order.valueOf(MyJFQL.getInstance().getFormatter().formatString(arguments.get("ORDER")).toUpperCase());
                } catch (Exception ex) {
                    remote.sendError("Unknown order type (DES, ASC)!");
                    return true;
                }

            }

            if (arguments.containsKey("DATABASES")) {
                if (!user.hasPermission("execute.list.databases")) {
                    return false;
                }

                List<String> strings = dataBaseService.getDataBases().stream().map(Database::getName).collect(Collectors.toList());

                if (limit != -1) {
                    strings = IntStream.range(0, limit).mapToObj(strings::get).collect(Collectors.toList());
                }

                remote.sendAnswer(sorter.sortList(strings, order), List.of("Databases"));
                return true;
            }
            if (arguments.containsKey("TABLES")) {
                if (!user.hasPermission("execute.list.tables")) {
                    return false;
                }

                if (arguments.containsKey("FROM")) {
                    String name = MyJFQL.getInstance().getFormatter().formatString(arguments.get("FROM"));

                    if (dataBaseService.getDataBase(name) == null) {
                        remote.sendError("Database doesn't exists!");
                        return true;
                    }

                    final Database database = dataBaseService.getDataBase(name);
                    List<String> strings = database.getTables().stream().map(Table::getName).collect(Collectors.toList());
                    if (limit != -1) {
                        strings = IntStream.range(0, limit).mapToObj(strings::get).collect(Collectors.toList());
                    }

                    remote.sendAnswer(sorter.sortList(strings, order), List.of("Tables"));
                } else {
                    List<String> strings = dataBaseService.getDataBases().stream().flatMap(dataBase -> dataBase.getTables().stream()).map(Table::getName).collect(Collectors.toList());
                    if (limit != -1) {
                        strings = IntStream.range(0, limit).mapToObj(strings::get).collect(Collectors.toList());
                    }

                    remote.sendAnswer(sorter.sortList(strings, order), List.of("Tables"));
                }

                return true;
            }
            if (arguments.containsKey("USERS")) {
                if (!user.hasPermission("execute.list.users")) {
                    return false;
                }

                List<String> strings = userService.getUsers().stream().map(User::getName).collect(Collectors.toList());
                if (limit != -1) {
                    strings = IntStream.range(0, limit).mapToObj(strings::get).collect(Collectors.toList());
                }

                remote.sendAnswer(sorter.sortList(strings, order), List.of("Users"));
                return true;
            }
            if (arguments.containsKey("SCRIPTS")) {
                if (!user.hasPermission("execute.list.scripts")) {
                    return false;
                }

                List<String> strings = scriptService.getScripts().stream().map(Script::getName).collect(Collectors.toList());
                if (limit != -1) {
                    strings = IntStream.range(0, limit).mapToObj(strings::get).collect(Collectors.toList());
                }

                remote.sendAnswer(sorter.sortList(strings, order), List.of("Scripts"));
                return true;
            }

            remote.sendSyntax();
        } else {
            ConsoleExecutor console = (ConsoleExecutor) executor;

            Sorter.Order order = Sorter.Order.ASC;
            int limit = -1;

            if (arguments.containsKey("LIMIT")) {
                limit = MyJFQL.getInstance().getFormatter().formatInteger(arguments.get("LIMIT"));

                if (limit <= -1) {
                    console.sendError("Limit can't be smaller than 0!");
                    return true;
                }
            }

            if (arguments.containsKey("ORDER")) {

                try {
                    order = Sorter.Order.valueOf(MyJFQL.getInstance().getFormatter().formatString(arguments.get("ORDER")).toUpperCase());
                } catch (Exception ex) {
                    console.sendError("Unknown order! Orders: ASC, DESC");
                    return true;
                }

            }

            if (arguments.containsKey("DATABASES")) {
                List<String> strings = dataBaseService.getDataBases().stream().map(Database::getName).collect(Collectors.toList());
                TablePrinter tablePrinter = new TablePrinter(1, "Databases");

                if (limit != -1) {
                    strings = IntStream.range(0, limit).mapToObj(strings::get).collect(Collectors.toList());
                }

                for (String string : sorter.sortList(strings, order)) {
                    tablePrinter.addRow(string);
                }

                tablePrinter.print();
                return true;
            }
            if (arguments.containsKey("TABLES")) {
                List<String> strings = dataBaseService.getDataBases().stream().flatMap(dataBase -> dataBase.getTables().stream()).map(Table::getName).collect(Collectors.toList());
                TablePrinter tablePrinter = new TablePrinter(1, "Tables");

                if (arguments.containsKey("FROM")) {
                    String name = MyJFQL.getInstance().getFormatter().formatString(arguments.get("FROM"));

                    if (dataBaseService.getDataBase(name) == null) {
                        console.sendError("Database doesn't exists!");
                        return true;
                    }

                    final Database database = dataBaseService.getDataBase(name);
                    List<String> strings1 = database.getTables().stream().map(Table::getName).collect(Collectors.toList());

                    if (limit != -1) {
                        strings1 = IntStream.range(0, limit).mapToObj(strings1::get).collect(Collectors.toList());
                    }

                    for (String string : sorter.sortList(strings1, order)) {
                        tablePrinter.addRow(string);
                    }

                } else {

                    if (limit != -1) {
                        strings = IntStream.range(0, limit).mapToObj(strings::get).collect(Collectors.toList());
                    }

                    for (String string : sorter.sortList(strings, order)) {
                        tablePrinter.addRow(string);
                    }
                }
                tablePrinter.print();
                return true;
            }
            if (arguments.containsKey("USERS")) {
                List<String> strings = userService.getUsers().stream().map(User::getName).collect(Collectors.toList());
                TablePrinter tablePrinter = new TablePrinter(1, "Users");

                if (limit != -1) {
                    strings = IntStream.range(0, limit).mapToObj(strings::get).collect(Collectors.toList());
                }

                for (String string : sorter.sortList(strings, order)) {
                    tablePrinter.addRow(string);
                }

                tablePrinter.print();
                return true;
            }
            if (arguments.containsKey("SCRIPTS")) {
                List<String> strings = scriptService.getScripts().stream().map(Script::getName).collect(Collectors.toList());
                TablePrinter tablePrinter = new TablePrinter(1, "Scripts");

                if (limit != -1) {
                    strings = IntStream.range(0, limit).mapToObj(strings::get).collect(Collectors.toList());
                }

                for (String string : sorter.sortList(strings, order)) {
                    tablePrinter.addRow(string);
                }

                tablePrinter.print();
                return true;
            }

            console.sendError("Unknown syntax!");

        }

        return true;
    }
}
