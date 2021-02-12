package org.jokergames.myjfql.command.executor;

import org.jokergames.myjfql.core.MyJFQL;

/**
 * @author Janick
 */

public class ConsoleExecutor extends Executor {

    public ConsoleExecutor() {
        super("Console");
    }

    public void sendError(String s) {
        MyJFQL.getInstance().getConsole().logError(s);
    }

    public void sendInfo(String s) {
        MyJFQL.getInstance().getConsole().logInfo(s);
    }

    public void send(String s) {
        MyJFQL.getInstance().getConsole().log(s);
    }

}
