package org.jokergames.myjfql.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class Command {

    private final String name;
    private final List<String> syntax;

    public Command(String name, List<String> syntax) {
        this.name = name;
        this.syntax = syntax;
    }

    public abstract void handle(final CommandSender sender, final Map<String, List<String>> args);

    public final String formatString(final List<String> strings) {
        if (strings.size() == 0)
            return null;

        final StringBuilder builder = new StringBuilder(strings.get(0));

        for (int i = 1; i < strings.size(); i++) {
            builder.append(" ").append(strings.get(i));
        }

        return builder.toString().replace("'", "");
    }

    public final List<String> formatList(final List<String> strings) {
        if (strings.size() == 0)
            return new ArrayList<>();

        return strings.stream().map(s -> s.replace("'", "")).collect(Collectors.toList());
    }

    public final int formatInteger(final List<String> strings) {
        if (strings.size() == 0)
            return -1;

        final StringBuilder builder = new StringBuilder(strings.get(0));

        for (int i = 1; i < strings.size(); i++) {
            builder.append(" ").append(strings.get(i));
        }

        return Integer.parseInt(builder.toString().replace("'", ""));
    }

    public final boolean formatBoolean(final List<String> strings) {
        if (strings.size() == 0)
            return false;

        final StringBuilder builder = new StringBuilder(strings.get(0));

        for (int i = 1; i < strings.size(); i++) {
            builder.append(" ").append(strings.get(i));
        }

        return Boolean.parseBoolean(builder.toString());
    }

    public final double formatDouble(final List<String> strings) {
        if (strings.size() == 0)
            return -1.0;

        final StringBuilder builder = new StringBuilder(strings.get(0));

        for (int i = 1; i < strings.size(); i++) {
            builder.append(" ").append(strings.get(i));
        }

        return Double.parseDouble(builder.toString().replace("'", ""));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command command = (Command) o;
        return Objects.equals(name, command.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Command{" +
                "name='" + name + '\'' +
                ", syntax=" + syntax +
                '}';
    }

    public List<String> getSyntax() {
        return syntax;
    }

    public String getName() {
        return name;
    }
}
