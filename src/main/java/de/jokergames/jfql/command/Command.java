package de.jokergames.jfql.command;

import de.jokergames.jfql.command.executor.Executor;
import de.jokergames.jfql.user.User;

import java.util.List;
import java.util.Map;

/**
 * @author Janick
 */

public abstract class Command {

    private final String name;
    private final List<String> syntax;

    public Command(String name, List<String> syntax) {
        this.name = name;
        this.syntax = syntax;
    }


    public abstract boolean handle(Executor executor, Map<String, List<String>> arguments, User user);

    public List<String> getSyntax() {
        return syntax;
    }

    public String getName() {
        return name;
    }
}
