package org.jokergames.jfql.command;

import org.jokergames.jfql.command.executor.Executor;
import org.jokergames.jfql.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Janick
 */

public abstract class Command {

    private final String name;
    private final List<String> syntax;
    private final List<String> aliases;

    public Command(String name, List<String> syntax) {
        this.name = name;
        this.syntax = syntax;
        this.aliases = new ArrayList<>();
    }

    public Command(String name, List<String> syntax, List<String> aliases) {
        this.name = name;
        this.syntax = syntax;
        this.aliases = aliases;
    }

    public abstract boolean handle(Executor executor, Map<String, List<String>> arguments, User user);

    public List<String> getSyntax() {
        return syntax;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public String getName() {
        return name;
    }
}
