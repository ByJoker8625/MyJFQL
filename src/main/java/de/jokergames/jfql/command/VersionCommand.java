package de.jokergames.jfql.command;

import de.jokergames.jfql.command.executor.Executor;
import de.jokergames.jfql.command.executor.RemoteExecutor;
import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.user.User;

import java.util.List;
import java.util.Map;

/**
 * @author Janick
 * @language This commands is not a JFQL query. It is only for the DBMS management.
 */

public class VersionCommand extends Command {


    public VersionCommand() {
        super("VERSION", List.of("COMMAND", "DISPLAY", "UPDATE"), List.of("VER"));
    }

    @Override
    public boolean handle(Executor executor, Map<String, List<String>> arguments, User user) {

        if (executor instanceof RemoteExecutor) {
            return false;
        }

        if (arguments.containsKey("DISPLAY")) {
            JFQL.getInstance().getConsole().logInfo("Current version: " + JFQL.getInstance().getVersion() + " (Downloaded: " + JFQL.getInstance().getConfiguration().getString("Date") + ")");
            return true;
        }

        if (arguments.containsKey("UPDATE")) {
            JFQL.getInstance().getDownloader().download();
            return true;
        }

        JFQL.getInstance().getConsole().logError("Unknown syntax!");
        return true;
    }
}
