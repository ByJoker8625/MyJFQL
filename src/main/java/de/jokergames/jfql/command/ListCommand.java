package de.jokergames.jfql.command;

import de.jokergames.jfql.command.executor.Executor;
import de.jokergames.jfql.command.executor.RemoteExecutor;
import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.database.Database;
import de.jokergames.jfql.database.DatabaseHandler;
import de.jokergames.jfql.database.Table;
import de.jokergames.jfql.exception.CommandException;
import de.jokergames.jfql.user.User;
import de.jokergames.jfql.user.UserHandler;
import de.jokergames.jfql.util.TablePrinter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Janick
 */

public class ListCommand extends Command {

    public ListCommand() {
        super("LIST", List.of("COMMAND", "DATABASES", "TABLES", "USERS", "FROM"));
    }

    @Override
    public boolean handle(Executor executor, Map<String, List<String>> arguments, User user) {
        final DatabaseHandler dataBaseHandler = JFQL.getInstance().getDataBaseHandler();
        final UserHandler userHandler = JFQL.getInstance().getUserHandler();

        if (executor instanceof RemoteExecutor) {
            RemoteExecutor remote = (RemoteExecutor) executor;

            if (!user.hasPermission("execute.list")) {
                return false;
            }

            if (arguments.containsKey("DATABASES")) {
                if (!user.hasPermission("execute.list.database")) {
                    return false;
                }

                List<String> strings = dataBaseHandler.getDataBases().stream().map(Database::getName).collect(Collectors.toList());
                remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildAnswer(strings, List.of("Databases")));
                return true;
            } else if (arguments.containsKey("TABLES")) {
                if (!user.hasPermission("execute.list.tables")) {
                    return false;
                }

                if (arguments.containsKey("FROM")) {
                    String name = JFQL.getInstance().getFormatter().formatString(arguments.get("FROM"));

                    if (dataBaseHandler.getDataBase(name) == null) {
                        remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Database doesn't exists!")));
                        return true;
                    }

                    final Database database = dataBaseHandler.getDataBase(name);
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildAnswer(database.getTables().stream().map(Table::getName).collect(Collectors.toList()), List.of("Tables")));
                } else {
                    List<String> strings = dataBaseHandler.getDataBases().stream().flatMap(dataBase -> dataBase.getTables().stream()).map(Table::getName).collect(Collectors.toList());
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildAnswer(strings, List.of("Tables")));
                }

                return true;
            } else if (arguments.containsKey("USERS")) {
                if (!user.hasPermission("execute.list.users")) {
                    return false;
                }

                List<String> strings = userHandler.getUsers().stream().map(User::getName).collect(Collectors.toList());
                remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildAnswer(strings, List.of("Users")));
                return true;
            }

            remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildSyntax());
        } else {

            if (arguments.containsKey("DATABASES")) {
                List<String> strings = dataBaseHandler.getDataBases().stream().map(Database::getName).collect(Collectors.toList());
                TablePrinter tablePrinter = new TablePrinter(1, "Databases");

                for (String string : strings) {
                    tablePrinter.addRow(string);
                }

                tablePrinter.print();
                return true;
            } else if (arguments.containsKey("TABLES")) {
                List<String> strings = dataBaseHandler.getDataBases().stream().flatMap(dataBase -> dataBase.getTables().stream()).map(Table::getName).collect(Collectors.toList());
                TablePrinter tablePrinter = new TablePrinter(1, "Tables");

                if (arguments.containsKey("FROM")) {
                    String name = JFQL.getInstance().getFormatter().formatString(arguments.get("FROM"));

                    if (dataBaseHandler.getDataBase(name) == null) {
                        JFQL.getInstance().getConsole().logError("Database doesn't exists!");
                        return true;
                    }

                    final Database database = dataBaseHandler.getDataBase(name);
                    List<String> strings1 = database.getTables().stream().map(Table::getName).collect(Collectors.toList());

                    for (String string : strings1) {
                        tablePrinter.addRow(string);
                    }

                } else {

                    for (String string : strings) {
                        tablePrinter.addRow(string);
                    }
                }
                tablePrinter.print();
                return true;
            } else if (arguments.containsKey("USERS")) {
                List<String> strings = userHandler.getUsers().stream().map(User::getName).collect(Collectors.toList());
                TablePrinter tablePrinter = new TablePrinter(1, "Users");

                for (String string : strings) {
                    tablePrinter.addRow(string);
                }

                tablePrinter.print();
                return true;
            }

            JFQL.getInstance().getConsole().logError("Unknown syntax!");
        }

        return true;
    }
}
