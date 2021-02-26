package org.jokergames.myjfql.command;

import org.jokergames.myjfql.command.executor.Executor;
import org.jokergames.myjfql.command.executor.RemoteExecutor;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.user.User;

import java.util.List;
import java.util.Map;

public class ClearCommand extends Command {

    public ClearCommand() {
        super("CLEAR", List.of("COMMAND"), List.of("CLS"));
    }

    @Override
    public boolean handle(Executor executor, Map<String, List<String>> arguments, User user) {
        if (executor instanceof RemoteExecutor) {
            return false;
        }

        MyJFQL.getInstance().getConsole().clear();
        return true;
    }
}
