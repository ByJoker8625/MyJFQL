package de.jokergames.jfql.command;

import de.jokergames.jfql.command.executor.ConsoleExecutor;
import de.jokergames.jfql.command.executor.Executor;
import de.jokergames.jfql.command.executor.RemoteExecutor;
import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.database.DatabaseService;
import de.jokergames.jfql.user.User;

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
        final DatabaseService dataBaseService = JFQL.getInstance().getDatabaseService();

        if (executor instanceof RemoteExecutor) {
            RemoteExecutor remote = (RemoteExecutor) executor;

            if (!user.hasPermission("execute.use")) {
                return false;
            }

            if (arguments.containsKey("DATABASE")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("DATABASE"));

                if (!user.hasPermission("execute.use.*") && !user.hasPermission("execute.use." + name)) {
                    return false;
                }

                if (dataBaseService.getDataBase(name) == null) {
                    remote.sendError("Database doesn't exists!");
                    return true;
                }

                JFQL.getInstance().getDBSession().put(user.getName(), name);
                remote.sendSuccess();
                return true;
            }

            remote.sendSyntax();
        } else {
            ConsoleExecutor console = (ConsoleExecutor) executor;

            if (arguments.containsKey("DATABASE")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("DATABASE"));

                if (dataBaseService.getDataBase(name) == null) {
                    console.sendError("Database '" + name + "' doesn't exists!");
                    return true;
                }

                JFQL.getInstance().getDBSession().put(user.getName(), name);
                console.sendInfo("Change database to '" + name + "'.");
                return true;
            }

            console.sendError("Unknown syntax!");
        }


        return true;
    }
}
