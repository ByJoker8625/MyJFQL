package de.jokergames.jfql.command;

import de.jokergames.jfql.command.executor.Executor;
import de.jokergames.jfql.command.executor.RemoteExecutor;
import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.database.Database;
import de.jokergames.jfql.database.DatabaseHandler;
import de.jokergames.jfql.exception.CommandException;
import de.jokergames.jfql.user.User;

import java.util.List;
import java.util.Map;

/**
 * @author Janick
 */

public class DeleteCommand extends Command {

    public DeleteCommand() {
        super("DELETE", List.of("COMMAND", "TABLE", "DATABASE", "FROM"));
    }

    @Override
    public boolean handle(Executor executor, Map<String, List<String>> arguments, User user) {
        final DatabaseHandler dataBaseHandler = JFQL.getInstance().getDataBaseHandler();

        if (executor instanceof RemoteExecutor) {
            RemoteExecutor remote = (RemoteExecutor) executor;

            if (!user.hasPermission("execute.delete")) {
                return false;
            }

            if (arguments.containsKey("DATABASE")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("DATABASE"));

                if (!user.hasPermission("execute.delete.database.*") && !user.hasPermission("execute.delete.database." + name)) {
                    return false;
                }

                if (dataBaseHandler.getDataBase(name) == null) {
                    remote.send(JFQL.getInstance().getBuilder().buildBadMethod(new CommandException("Database doesn't exists!")));
                    return true;
                }

                remote.send(JFQL.getInstance().getBuilder().buildSuccess());
                dataBaseHandler.getDataBase(name).getFile().delete();
                return true;
            }

            if (arguments.containsKey("TABLE")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("TABLE"));
                String base;

                if (!user.hasPermission("execute.delete.table.*") && !user.hasPermission("execute.delete.table." + name)) {
                    return false;
                }

                if (arguments.containsKey("FROM")) {
                    base = JFQL.getInstance().getFormatter().formatString(arguments.get("FROM"));
                } else {
                    base = JFQL.getInstance().getDBSession().get(user.getName());
                }

                if (dataBaseHandler.getDataBase(base) == null) {
                    remote.send(JFQL.getInstance().getBuilder().buildBadMethod(new CommandException("Database doesn't exists!")));
                    return true;
                }

                final Database dataBase = dataBaseHandler.getDataBase(base);

                if (dataBase.getTable(name) == null) {
                    remote.send(JFQL.getInstance().getBuilder().buildBadMethod(new CommandException("Table doesn't exists!")));
                    return true;
                }

                remote.send(JFQL.getInstance().getBuilder().buildSuccess());
                dataBase.removeTable(name);
                dataBaseHandler.saveDataBase(dataBase);
                return true;
            }

            remote.send(JFQL.getInstance().getBuilder().buildSyntax());
        } else {
            if (arguments.containsKey("DATABASE")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("DATABASE"));

                if (dataBaseHandler.getDataBase(name) == null) {
                    JFQL.getInstance().getConsole().logError("Database '" + name + "' was not found!");
                    return true;
                }

                JFQL.getInstance().getConsole().logInfo("Database '" + name + "' was deleted.");
                dataBaseHandler.getDataBase(name).getFile().delete();
                return true;
            }

            if (arguments.containsKey("TABLE")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("TABLE"));
                String base;

                if (arguments.containsKey("FROM")) {
                    base = JFQL.getInstance().getFormatter().formatString(arguments.get("FROM"));
                } else {
                    base = JFQL.getInstance().getDBSession().get(user.getName());
                }

                if (dataBaseHandler.getDataBase(base) == null) {
                    JFQL.getInstance().getConsole().logError("Database '" + name + "' was not found!");
                    return true;
                }

                final Database dataBase = dataBaseHandler.getDataBase(base);

                if (dataBase.getTable(name) == null) {
                    JFQL.getInstance().getConsole().logError("Table '" + name + "' doesn't exists!");
                    return true;
                }

                JFQL.getInstance().getConsole().logInfo("Table '" + name + "' was deleted.");
                dataBase.removeTable(name);
                dataBaseHandler.saveDataBase(dataBase);
                return true;
            }

            JFQL.getInstance().getConsole().logError("Unknown syntax!");
        }

        return true;
    }
}
