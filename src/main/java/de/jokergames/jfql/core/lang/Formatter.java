package de.jokergames.jfql.core.lang;

import de.jokergames.jfql.core.JFQL;

import java.util.*;

/**
 * @author Janick
 * @language JavaFileQueryLanguage (JFQL)
 * @data 9.11.2020
 */

public class Formatter {


    public Map<String, List<String>> formatCommand(String command) {
        Map<String, List<String>> arguments = new HashMap<>();

        String[] cmd = command.split(" ");

        List<String> strings = new ArrayList<>();
        String string = null;

        for (int i = 0; i < cmd.length; i++) {
            String current = cmd[i];

            if (i == 0) {
                strings.add("COMMAND");
                strings.add(current);
            } else {
                if (current.startsWith("'") && current.endsWith("'")) {
                    strings.add(current);
                } else if (current.startsWith("'") && !current.endsWith("'")) {
                    string = current;
                } else if (!current.startsWith("'") && current.endsWith("'")) {
                    string += " " + current;
                    strings.add(string);
                    string = null;
                } else {
                    if (string == null) {
                        strings.add(current);
                    } else {
                        string += " " + current;
                    }
                }

            }

        }

        List<String> keys;

        try {
            keys = JFQL.getInstance().getCommandService().getCommand(strings.get(1)).getSyntax();
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
                    throw new RuntimeException();
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

        String string = strings.get(0);

        for (int i = 1; i < strings.size(); i++) {
            string += " " + strings.get(i);
        }

        return string.replace("'", "");
    }

    public int formatInteger(List<String> strings) {
        if (strings.size() == 0)
            return -1;

        String string = strings.get(0);

        for (int i = 1; i < strings.size(); i++) {
            string += " " + strings.get(i);
        }

        return Integer.parseInt(string.replace("'", ""));
    }

    public boolean formatBoolean(List<String> strings) {
        if (strings.size() == 0)
            return false;

        String string = strings.get(0);

        for (int i = 1; i < strings.size(); i++) {
            string += " " + strings.get(i);
        }

        return Boolean.parseBoolean(string);
    }

    public double formatDouble(List<String> strings) {
        if (strings.size() == 0)
            return -1.0;

        String string = strings.get(0);

        for (int i = 1; i < strings.size(); i++) {
            string += " " + strings.get(i);
        }

        return Double.parseDouble(string.replace("'", ""));
    }

}
