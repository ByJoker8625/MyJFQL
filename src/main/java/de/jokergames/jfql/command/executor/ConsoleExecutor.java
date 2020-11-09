package de.jokergames.jfql.command.executor;

import de.jokergames.jfql.core.JFQL;

/**
 * @author Janick
 */

public class ConsoleExecutor extends Executor {

    public ConsoleExecutor() {
        super("Console");
    }

    public void sendError(String s) {
        JFQL.getInstance().getConsole().logError(s);
    }

    public void sendInfo(String s) {
        JFQL.getInstance().getConsole().logInfo(s);
    }

    public void sandWarning(String s) {
        JFQL.getInstance().getConsole().logWarning(s);
    }

}
