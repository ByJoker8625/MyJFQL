package org.jokergames.myjfql.command;

import org.jokergames.myjfql.command.executor.Executor;
import org.jokergames.myjfql.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command command = (Command) o;
        return Objects.equals(name, command.name) &&
                Objects.equals(syntax, command.syntax) &&
                Objects.equals(aliases, command.aliases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, syntax, aliases);
    }

    @Override
    public String toString() {
        return "Command{" +
                "name='" + name + '\'' +
                ", syntax=" + syntax +
                ", aliases=" + aliases +
                '}';
    }

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
