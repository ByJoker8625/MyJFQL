package org.jokergames.myjfql.command;

import org.jokergames.myjfql.command.executor.ConsoleExecutor;
import org.jokergames.myjfql.command.executor.Executor;
import org.jokergames.myjfql.command.executor.RemoteExecutor;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.database.DatabaseService;
import org.jokergames.myjfql.user.User;

import java.util.List;
import java.util.Map;

/**
 * @author Janick
 */

public class UseCommand extends Command {

    public UseCommand() {
        super("USE", List.of("COMMAND", "DATABASE"));
    }

    @Override
    public boolean handle(Executor executor, Map<String, List<String>> arguments, User user) {
        final DatabaseService dataBaseService = MyJFQL.getInstance().getDatabaseService();

        if (executor instanceof RemoteExecutor) {
            RemoteExecutor remote = (RemoteExecutor) executor;

            if (!user.hasPermission("execute.use")) {
                return false;
            }

            if (arguments.containsKey("DATABASE")) {
                String name = MyJFQL.getInstance().getFormatter().formatString(arguments.get("DATABASE"));

                if (!user.hasPermission("execute.use.*") && !user.hasPermission("execute.use." + name)) {
                    return false;
                }

                if (dataBaseService.getDataBase(name) == null) {
                    remote.sendError("Database doesn't exists!");
                    return true;
                }

                MyJFQL.getInstance().getDBSession().put(user.getName(), name);
                remote.sendSuccess();
                return true;
            }

            remote.sendSyntax();
        } else {
            ConsoleExecutor console = (ConsoleExecutor) executor;

            if (arguments.containsKey("DATABASE")) {
                String name = MyJFQL.getInstance().getFormatter().formatString(arguments.get("DATABASE"));

                if (dataBaseService.getDataBase(name) == null) {
                    console.sendError("Database '" + name + "' doesn't exists!");
                    return true;
                }

                MyJFQL.getInstance().getDBSession().put(user.getName(), name);
                console.sendInfo("Change database to '" + name + "'.");
                return true;
            }

            console.sendError("Unknown syntax!");
        }


        return true;
    }
}
