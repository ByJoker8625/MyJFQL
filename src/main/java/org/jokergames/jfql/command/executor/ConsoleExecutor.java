package org.jokergames.jfql.command.executor;

import org.jokergames.jfql.core.JFQL;

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

    public void send(String s) {
        JFQL.getInstance().getConsole().log(s);
    }

}
