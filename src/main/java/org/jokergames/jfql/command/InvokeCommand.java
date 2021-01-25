package org.jokergames.jfql.command;

import org.jokergames.jfql.command.executor.ConsoleExecutor;
import org.jokergames.jfql.command.executor.Executor;
import org.jokergames.jfql.command.executor.RemoteExecutor;
import org.jokergames.jfql.core.JFQL;
import org.jokergames.jfql.core.script.ScriptService;
import org.jokergames.jfql.user.User;
import org.jokergames.jfql.user.UserService;

import java.util.List;
import java.util.Map;

/**
 * @author Janick
 */

public class InvokeCommand extends Command {

    public InvokeCommand() {
        super("INVOKE", List.of("COMMAND", "SCRIPT", "AS", "DISPLAY"), List.of("INV"));
    }

    @Override
    public boolean handle(Executor executor, Map<String, List<String>> arguments, User user) {
        final ScriptService scriptService = JFQL.getInstance().getScriptService();
        final UserService userService = JFQL.getInstance().getUserService();

        if (executor instanceof RemoteExecutor) {
            RemoteExecutor remote = (RemoteExecutor) executor;

            if (!user.hasPermission("execute.invoke")) {
                return false;
            }

            if (arguments.containsKey("SCRIPT")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("SCRIPT"));

                if (!user.hasPermission("execute.invoke.script.*") && !user.hasPermission("execute.invoke.script." + name)) {
                    return false;
                }

                if (scriptService.getScript(name) == null) {
                    remote.sendError("Script doesn't exists!");
                    return true;
                }

                remote.sendSuccess();
                scriptService.invokeScript(name, user, false);
                return true;
            }

            remote.sendSyntax();
        } else {
            ConsoleExecutor console = (ConsoleExecutor) executor;

            if (arguments.containsKey("SCRIPT")) {
                String name = JFQL.getInstance().getFormatter().formatString(arguments.get("SCRIPT"));
                boolean display = false;

                if (scriptService.getScript(name) == null) {
                    console.sendError("Script '" + name + "' doesn't exists!");
                    return true;
                }

                User usr = user;

                if (arguments.containsKey("AS")) {
                    String as = JFQL.getInstance().getFormatter().formatString(arguments.get("AS"));

                    if (userService.getUser(as) == null) {
                        console.sendError("User '" + as + "' doesn't exists!");
                        return true;
                    }

                    usr = userService.getUser(as);
                }

                if (arguments.containsKey("DISPLAY")) {
                    display = JFQL.getInstance().getFormatter().formatBoolean(arguments.get("DISPLAY"));
                }

                console.sendInfo("Invoking script '" + name + "'...");
                scriptService.invokeScript(name, usr, display);
                console.sendInfo("Script successful executed.");
                return true;
            }

            console.sendError("Unknown syntax!");
        }

        return true;
    }
}
