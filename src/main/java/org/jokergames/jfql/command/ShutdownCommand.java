package org.jokergames.jfql.command;

import org.jokergames.jfql.command.executor.Executor;
import org.jokergames.jfql.command.executor.RemoteExecutor;
import org.jokergames.jfql.core.JFQL;
import org.jokergames.jfql.user.User;

import java.util.List;
import java.util.Map;

/**
 * @author Janick
 * @language This commands is not a JFQL query. It is only for the DBMS management.
 */

public class ShutdownCommand extends Command {

    public ShutdownCommand() {
        super("SHUTDOWN", List.of("COMMAND"), List.of("STOP"));
    }

    @Override
    public boolean handle(Executor executor, Map<String, List<String>> arguments, User user) {
        if (executor instanceof RemoteExecutor) {
            return false;
        }

        JFQL.getInstance().shutdown();
        return true;
    }
}
