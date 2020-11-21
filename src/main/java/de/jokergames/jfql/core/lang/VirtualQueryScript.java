package de.jokergames.jfql.core.lang;

import de.jokergames.jfql.command.executor.ConsoleExecutor;
import de.jokergames.jfql.command.executor.Executor;
import de.jokergames.jfql.command.executor.RemoteExecutor;
import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.exception.CommandException;
import de.jokergames.jfql.user.ConsoleUser;
import de.jokergames.jfql.user.User;

import java.util.List;

/**
 * @author Janick
 */

public class VirtualQueryScript {

    private final User user;
    private final Executor executor;

    public VirtualQueryScript(User user, boolean wcp) {
        this.user = user;

        if (user instanceof ConsoleUser && wcp) {
            this.executor = new ConsoleExecutor();
        } else
            this.executor = new RemoteExecutor(user.getName(), null);
    }

    public VirtualQueryScript(User user) {
        this(user, false);
    }

    public void invokeScript(List<String> script) {
        for (int i = 0; i < script.size(); i++) {
            invokeLineScript(script, i);
        }
    }

    public void invokeLineScript(List<String> script, int line) {
        if (line == -1) {
            invokeScript(script);
            return;
        }

        final String query = script.get(line);

        try {
            JFQL.getInstance().getConsole().logInfo("Performing vqs [\"" + query + "\"]");
            JFQL.getInstance().getCommandService().execute(user, executor, JFQL.getInstance().getFormatter().formatCommand(query));
        } catch (Exception ex) {
            new CommandException(ex).printStackTrace();
        }

    }

    public Executor getExecutor() {
        return executor;
    }

    public User getUser() {
        return user;
    }
}
