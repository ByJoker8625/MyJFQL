package de.jokergames.jfql.command;

import de.jokergames.jfql.command.executor.Executor;
import de.jokergames.jfql.command.executor.RemoteExecutor;
import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.database.Column;
import de.jokergames.jfql.database.Database;
import de.jokergames.jfql.database.DatabaseHandler;
import de.jokergames.jfql.database.Table;
import de.jokergames.jfql.exception.CommandException;
import de.jokergames.jfql.user.User;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Janick
 */

public class RemoveCommand extends Command {

    public RemoveCommand() {
        super("REMOVE", List.of("COMMAND", "WHERE", "FROM", "COLUMN"), List.of("REM"));
    }


    @Override
    public boolean handle(Executor executor, Map<String, List<String>> arguments, User user) {
        final DatabaseHandler dataBaseHandler = JFQL.getInstance().getDataBaseHandler();

        if (executor instanceof RemoteExecutor) {
            RemoteExecutor remote = (RemoteExecutor) executor;

            if (!user.hasPermission("execute.remove")) {
                return false;
            }

            if (arguments.containsKey("FROM") && arguments.containsKey("COLUMN")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("FROM"));
                String column = JFQL.getInstance().getFormatter().formatString(arguments.get("COLUMN"));

                final Database dataBase = dataBaseHandler.getDataBase(JFQL.getInstance().getDbSession().get(user.getName()));

                if (dataBase.getTable(name) == null) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Table doesn't exists!")));
                    return true;
                }

                final Table table = dataBase.getTable(name);

                if (!user.hasPermission("execute.remove.database." + dataBase.getName() + ".*") && !user.hasPermission("execute.remove.database." + dataBase.getName() + "." + table.getName())) {
                    return false;
                }

                if (table.getColumn(column) == null && !column.equals("*")) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Unknown column!")));
                    return true;
                }

                if (arguments.containsKey("WHERE")) {
                    List<Column> columns = null;

                    try {
                        columns = JFQL.getInstance().getConditionHelper().getRequiredColumns(table, arguments.get("WHERE"));
                    } catch (Exception ex) {
                        remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Unknown 'where' error!")));
                        return true;
                    }

                    if (columns == null) {
                        remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Unknown 'where' error!")));
                        return true;
                    }

                    for (Column col : columns) {
                        table.removeColumn(col.getContent(table.getPrimary()).toString());
                    }

                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildSuccess());
                    dataBase.addTable(table);
                    dataBaseHandler.saveDataBase(dataBase);
                } else {
                    List<Column> columns;

                    if (column.equals("*")) {
                        columns = table.getColumns();
                    } else {
                        columns = Collections.singletonList(table.getColumn(column));
                    }

                    for (Column col : columns) {
                        table.removeColumn(col.getContent(table.getPrimary()).toString());
                    }

                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildSuccess());
                    dataBase.addTable(table);
                    dataBaseHandler.saveDataBase(dataBase);
                }

                return true;
            }

            remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildSyntax());
        } else {

            if (arguments.containsKey("FROM") && arguments.containsKey("COLUMN")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("FROM"));
                String column = JFQL.getInstance().getFormatter().formatString(arguments.get("COLUMN"));

                final Database dataBase = dataBaseHandler.getDataBase(JFQL.getInstance().getDbSession().get(user.getName()));

                if (dataBase.getTable(name) == null) {
                    JFQL.getInstance().getConsole().logError("Table '" + name + "' doesn't exists!");
                    return true;
                }

                final Table table = dataBase.getTable(name);

                if (table.getColumn(column) == null && !column.equals("*")) {
                    JFQL.getInstance().getConsole().logError("Column '" + column + "' doesn't exists!");
                    return true;
                }

                if (arguments.containsKey("WHERE")) {
                    List<Column> columns = null;

                    try {
                        columns = JFQL.getInstance().getConditionHelper().getRequiredColumns(table, arguments.get("WHERE"));
                    } catch (Exception ex) {
                        JFQL.getInstance().getConsole().logError("Unknown error!");
                        return true;
                    }

                    if (columns == null) {
                        JFQL.getInstance().getConsole().logError("Unknown key!");
                        return true;
                    }

                    for (Column col : columns) {
                        table.removeColumn(col.getContent(table.getPrimary()).toString());
                    }

                    JFQL.getInstance().getConsole().logInfo("Column/s was removed.");
                    dataBase.addTable(table);
                    dataBaseHandler.saveDataBase(dataBase);
                } else {
                    List<Column> columns;

                    if (column.equals("*")) {
                        columns = table.getColumns();
                    } else {
                        columns = Collections.singletonList(table.getColumn(column));
                    }

                    for (Column col : columns) {
                        table.removeColumn(col.getContent(table.getPrimary()).toString());
                    }

                    JFQL.getInstance().getConsole().logInfo("Column/s was removed.");
                    dataBase.addTable(table);
                    dataBaseHandler.saveDataBase(dataBase);
                }

                return true;
            }

            JFQL.getInstance().getConsole().logError("Unknown syntax!");
        }

        return true;
    }
}
