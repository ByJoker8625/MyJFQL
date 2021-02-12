package org.jokergames.myjfql.command;

import org.jokergames.myjfql.command.executor.ConsoleExecutor;
import org.jokergames.myjfql.command.executor.Executor;
import org.jokergames.myjfql.command.executor.RemoteExecutor;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.database.Database;
import org.jokergames.myjfql.database.DatabaseService;
import org.jokergames.myjfql.script.ScriptService;
import org.jokergames.myjfql.user.User;

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
        final DatabaseService dataBaseService = MyJFQL.getInstance().getDatabaseService();
        final ScriptService scriptService = MyJFQL.getInstance().getScriptService();

        if (executor instanceof RemoteExecutor) {
            RemoteExecutor remote = (RemoteExecutor) executor;

            if (!user.hasPermission("execute.delete")) {
                return false;
            }

            if (arguments.containsKey("SCRIPT")) {
                String name = MyJFQL.getInstance().getFormatter().formatString(arguments.get("SCRIPT"));

                if (!user.hasPermission("execute.delete.script.*") && !user.hasPermission("execute.delete.script." + name)) {
                    return false;
                }

                if (scriptService.getScript(name) == null) {
                    remote.sendError("Script doesn't exists!");
                    return true;
                }

                remote.sendSuccess();
                scriptService.getScript(name).getFile().delete();
                return true;
            }

            if (arguments.containsKey("DATABASE")) {
                String name = MyJFQL.getInstance().getFormatter().formatString(arguments.get("DATABASE"));

                if (!user.hasPermission("execute.delete.database.*") && !user.hasPermission("execute.delete.database." + name)) {
                    return false;
                }

                if (dataBaseService.getDataBase(name) == null) {
                    remote.sendError("Database doesn't exists!");
                    return true;
                }

                remote.sendSuccess();
                dataBaseService.getDataBase(name).getFile().delete();
                return true;
            }

            if (arguments.containsKey("TABLE")) {
                String name = MyJFQL.getInstance().getFormatter().formatString(arguments.get("TABLE"));
                String base;

                if (!user.hasPermission("execute.delete.table.*") && !user.hasPermission("execute.delete.table." + name)) {
                    return false;
                }

                if (arguments.containsKey("FROM")) {
                    base = MyJFQL.getInstance().getFormatter().formatString(arguments.get("FROM"));
                } else {
                    base = MyJFQL.getInstance().getDBSession().get(user.getName());
                }

                if (!user.hasPermission("execute.use.database.*") && !user.hasPermission("execute.use.database." + base)) {
                    return false;
                }

                if (dataBaseService.getDataBase(base) == null) {
                    remote.sendError("Database doesn't exists!");
                    return true;
                }

                final Database dataBase = dataBaseService.getDataBase(base);

                if (dataBase.getTable(name) == null) {
                    remote.sendError("Table doesn't exists!");
                    return true;
                }

                remote.sendSuccess();
                dataBase.removeTable(name);
                dataBaseService.saveDataBase(dataBase);
                return true;
            }

            remote.sendSyntax();
        } else {
            ConsoleExecutor console = (ConsoleExecutor) executor;

            if (arguments.containsKey("SCRIPT")) {
                String name = MyJFQL.getInstance().getFormatter().formatString(arguments.get("SCRIPT"));

                if (scriptService.getScript(name) == null) {
                    console.sendError("Script '" + name + "' doesn't exists!");
                    return true;
                }

                console.sendInfo("Delete script '" + name + "'.");
                scriptService.getScript(name).getFile().delete();
                return true;
            }

            if (arguments.containsKey("DATABASE")) {
                String name = MyJFQL.getInstance().getFormatter().formatString(arguments.get("DATABASE"));

                if (dataBaseService.getDataBase(name) == null) {
                    console.sendError("Database '" + name + "' was not found!");
                    return true;
                }

                console.sendInfo("Database '" + name + "' was deleted.");
                dataBaseService.getDataBase(name).getFile().delete();
                return true;
            }

            if (arguments.containsKey("TABLE")) {
                String name = MyJFQL.getInstance().getFormatter().formatString(arguments.get("TABLE"));
                String base;

                if (arguments.containsKey("FROM")) {
                    base = MyJFQL.getInstance().getFormatter().formatString(arguments.get("FROM"));
                } else {
                    base = MyJFQL.getInstance().getDBSession().get(user.getName());
                }

                if (dataBaseService.getDataBase(base) == null) {
                    console.sendError("Database '" + name + "' was not found!");
                    return true;
                }

                final Database dataBase = dataBaseService.getDataBase(base);

                if (dataBase.getTable(name) == null) {
                    console.sendError("Table '" + name + "' doesn't exists!");
                    return true;
                }

                console.sendInfo("Table '" + name + "' was deleted.");
                dataBase.removeTable(name);
                dataBaseService.saveDataBase(dataBase);
                return true;
            }

            console.sendError("Unknown syntax!");
        }

        return true;
    }
}
