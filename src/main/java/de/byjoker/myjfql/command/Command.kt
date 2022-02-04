package de.byjoker.myjfql.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class Command {

    private final String name;
    private final List<String> syntax;

    public Command(String name, List<String> syntax) {
        this.name = name;
        this.syntax = syntax;
    }

    public abstract void handleCommand(CommandSender sender, Map<String, List<String>> args);

    public final String formatString(List<String> strings) {
        if (strings.size() == 0)
            return null;

        return IntStream.range(1, strings.size()).mapToObj(i -> " " + strings.get(i)).collect(Collectors.joining("", strings.get(0), "")).replace("'", "");
    }

    public final List<String> formatList(List<String> strings) {
        if (strings.size() == 0)
            return new ArrayList<>();

        return strings.stream().map(s -> s.replace("'", "")).collect(Collectors.toList());
    }

    public final int formatInteger(List<String> strings) {
        if (strings.size() == 0)
            return -1;

        return Integer.parseInt(IntStream.range(1, strings.size()).mapToObj(i -> " " + strings.get(i)).collect(Collectors.joining("", strings.get(0), "")).replace("'", ""));
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
