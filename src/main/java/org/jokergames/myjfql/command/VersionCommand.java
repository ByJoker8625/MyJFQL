package org.jokergames.myjfql.command;

import org.jokergames.myjfql.command.executor.Executor;
import org.jokergames.myjfql.command.executor.RemoteExecutor;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.user.User;

import java.util.List;
import java.util.Map;

/**
 * @author Janick
 * @language This commands is not a JFQL query. It is only for the DBMS management.
 */

public class VersionCommand extends Command {


    public VersionCommand() {
        super("VERSION", List.of("COMMAND", "DISPLAY", "UPDATE"));
    }

    @Override
    public boolean handle(Executor executor, Map<String, List<String>> arguments, User user) {

        if (executor instanceof RemoteExecutor) {
            return false;
        }

        if (arguments.containsKey("DISPLAY")) {
            MyJFQL.getInstance().getConsole().logInfo("Current version: " + MyJFQL.getInstance().getVersion() + " (Downloaded: " + MyJFQL.getInstance().getConfiguration().getString("Date") + ")");
            return true;
        }

        if (arguments.containsKey("UPDATE")) {
            MyJFQL.getInstance().getDownloader().download();
            return true;
        }

        MyJFQL.getInstance().getConsole().logError("Unknown syntax!");
        return true;
    }
}
