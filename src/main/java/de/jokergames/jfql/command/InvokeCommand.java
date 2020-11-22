package de.jokergames.jfql.command;

import de.jokergames.jfql.command.executor.Executor;
import de.jokergames.jfql.command.executor.RemoteExecutor;
import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.core.script.ScriptService;
import de.jokergames.jfql.exception.CommandException;
import de.jokergames.jfql.user.User;

import java.util.List;
import java.util.Map;

/**
 * @author Janick
 */

public class InvokeCommand extends Command {

    public InvokeCommand() {
        super("INVOKE", List.of("COMMAND", "SCRIPT"));
    }

    @Override
    public boolean handle(Executor executor, Map<String, List<String>> arguments, User user) {
        final ScriptService scriptService = JFQL.getInstance().getScriptService();

        if (executor instanceof RemoteExecutor) {
            RemoteExecutor remote = (RemoteExecutor) executor;

            if (!user.hasPermission("execute.script")) {
                return false;
            }

            if (arguments.containsKey("SCRIPT")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("SCRIPT"));

                if (!user.hasPermission("execute.script.*") && !user.hasPermission("execute.script." + name)) {
                    return false;
                }

                if (scriptService.getScript(name) == null) {
                    remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildBadMethod(new CommandException("Script doesn't exists!")));
                    return true;
                }

                remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildSuccess());
                scriptService.invokeScript(name, user, false);
                return true;
            }

            remote.send(JFQL.getInstance().getJavalinService().getResponseBuilder().buildSyntax());
        } else {

            if (arguments.containsKey("SCRIPT")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("SCRIPT"));

                if (scriptService.getScript(name) == null) {
                    JFQL.getInstance().getConsole().logError("Script '" + name + "' doesn't exists!");
                    return true;
                }

                JFQL.getInstance().getConsole().logInfo("Invoking script '" + name + "'...");
                scriptService.invokeScript(name, user, false);
                JFQL.getInstance().getConsole().logInfo("Script successful executed.");
                return true;
            }

            JFQL.getInstance().getConsole().logError("Unknown syntax!");
        }

        return true;
    }
}
