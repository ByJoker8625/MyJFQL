package de.jokergames.jfql.command;

import de.jokergames.jfql.command.executor.Executor;
import de.jokergames.jfql.command.executor.RemoteExecutor;
import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.core.script.ScriptService;
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
        super("DELETE", List.of("COMMAND", "SCRIPT", "TABLE", "DATABASE", "FROM"), List.of("DEL"));
    }

    @Override
    public boolean handle(Executor executor, Map<String, List<String>> arguments, User user) {
        final DatabaseHandler dataBaseHandler = JFQL.getInstance().getDataBaseHandler();
        final ScriptService scriptService = JFQL.getInstance().getScriptService();

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
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Database doesn't exists!")));
                    return true;
                }

                remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildSuccess());
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
                    base = JFQL.getInstance().getDbSession().get(user.getName());
                }

                if (dataBaseHandler.getDataBase(base) == null) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Database doesn't exists!")));
                    return true;
                }

                final Database dataBase = dataBaseHandler.getDataBase(base);

                if (dataBase.getTable(name) == null) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Table doesn't exists!")));
                    return true;
                }

                remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildSuccess());
                dataBase.removeTable(name);
                dataBaseHandler.saveDataBase(dataBase);
                return true;
            }

            if (arguments.containsKey("SCRIPT")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("SCRIPT"));

                if (!user.hasPermission("execute.delete.script.*") && !user.hasPermission("execute.delete.script." + name)) {
                    return false;
                }

                if (scriptService.getScript(name) == null) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Script doesn't exists!")));
                    return true;
                }

                remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildSuccess());
                scriptService.getScript(name).getFile().delete();
                return true;
            }

            remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildSyntax());
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
                    base = JFQL.getInstance().getDbSession().get(user.getName());
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

            if (arguments.containsKey("SCRIPT")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("SCRIPT"));

                if (scriptService.getScript(name) == null) {
                    JFQL.getInstance().getConsole().logError("Script '" + name + "' doesn't exists!");
                    return true;
                }

                JFQL.getInstance().getConsole().logInfo("Delete script '" + name + "'.");
                scriptService.getScript(name).getFile().delete();
                return true;
            }

            JFQL.getInstance().getConsole().logError("Unknown syntax!");
        }

        return true;
    }
}
