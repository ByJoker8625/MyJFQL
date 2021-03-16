package org.jokergames.myjfql.core.lang;

import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.exception.CommandException;

import java.util.*;

/**
 * @author Janick
 * @language JavaFileQueryLanguage (JFQL)
 * @data 9.11.2020
 */

public class Formatter {


    public Map<String, List<String>> formatCommand(String command) {
        if (command.startsWith("#")) {
            return Map.of("COMMAND", List.of("#"));
        }

        Map<String, List<String>> arguments = new HashMap<>();

        String[] cmd = command.split(" ");

        List<String> strings = new ArrayList<>();
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

        List<String> keys;

        try {
            keys = MyJFQL.getInstance().getCommandService().getCommand(strings.get(1)).getSyntax();
        } catch (Exception ex) {
            return null;
        }

        String section = null;

        for (String current : strings) {
            if (keys.contains(current.toUpperCase())) {
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

    public String formatString(List<String> strings) {
        if (strings.size() == 0)
            return null;

        StringBuilder builder = new StringBuilder(strings.get(0));

        for (int i = 1; i < strings.size(); i++) {
            builder.append(" ").append(strings.get(i));
        }

        return builder.toString().replace("'", "");
    }

    public int formatInteger(List<String> strings) {
        if (strings.size() == 0)
            return -1;

        StringBuilder builder = new StringBuilder(strings.get(0));

        for (int i = 1; i < strings.size(); i++) {
            builder.append(" ").append(strings.get(i));
        }

        return Integer.parseInt(builder.toString().replace("'", ""));
    }

    public boolean formatBoolean(List<String> strings) {
        if (strings.size() == 0)
            return false;

        StringBuilder builder = new StringBuilder(strings.get(0));

        for (int i = 1; i < strings.size(); i++) {
            builder.append(" ").append(strings.get(i));
        }

        return Boolean.parseBoolean(builder.toString());
    }

    public double formatDouble(List<String> strings) {
        if (strings.size() == 0)
            return -1.0;

        StringBuilder builder = new StringBuilder(strings.get(0));

        for (int i = 1; i < strings.size(); i++) {
            builder.append(" ").append(strings.get(i));
        }

        return Double.parseDouble(builder.toString().replace("'", ""));
    }

}
