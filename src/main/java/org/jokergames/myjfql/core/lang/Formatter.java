package org.jokergames.myjfql.core.lang;

import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.exception.CommandException;

import java.util.*;
import java.util.stream.Collectors;

public class Formatter {

    public Map<String, List<String>> formatCommand(final String command) {
        if (command == null)
            return null;

        final String[] cmd = command.split(" ");

        final List<String> strings = new ArrayList<>();
        StringBuilder builder = null;

        for (int i = 0; i < cmd.length; i++) {
            String current = cmd[i];

            if (i == 0) {
                strings.add("COMMAND");
                strings.add(current);
            } else {
                if (current.startsWith("'") && current.endsWith("'")) {
                    strings.add(current);
                } else if (current.startsWith("'") && !current.endsWith("'")) {
                    builder = new StringBuilder(current);
                } else if (!current.startsWith("'") && current.endsWith("'")) {
                    assert builder != null;

                    builder.append(" ").append(current);
                    strings.add(builder.toString());
                    builder = null;
                } else {
                    if (builder == null) {
                        strings.add(current);
                    } else {
                        builder.append(" ").append(current);
                    }
                }

            }

        }

        List<String> syntax;

        try {
            syntax = MyJFQL.getInstance().getCommandService().getCommand(strings.get(1)).getSyntax();
        } catch (Exception ex) {
            return null;
        }

        final Map<String, List<String>> arguments = new HashMap<>();
        String section = null;

        for (final String current : strings) {
            if (syntax.contains(current.toUpperCase())) {
                section = current.toUpperCase();
                arguments.put(section, new ArrayList<>());
            } else {
                if (section == null) {
                    throw new CommandException();
                }

                if (!arguments.containsKey(section)) {
                    arguments.put(section, Collections.singletonList(current));
                } else {
                    arguments.get(section).add(current);
                }
            }
        }

        return arguments;
    }

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


}
