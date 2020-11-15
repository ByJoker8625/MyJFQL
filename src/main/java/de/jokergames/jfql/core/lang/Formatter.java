package de.jokergames.jfql.core.lang;

import de.jokergames.jfql.core.JFQL;

import java.util.*;

/**
 * @author Janick
 * @language JavaFileQueryLanguage (JFQL)
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

        List<String> keyWords;

        try {
            keyWords = JFQL.getInstance().getCommandHandler().getCommand(strings.get(1)).getSyntax();
        } catch (Exception ex) {
            return null;
        }

        String[] args = new String[strings.size()];
        int index = 0;

        for (String s : strings) {
            args[index] = s;
            index++;
        }

        String selection = null;

        for (String current : args) {
            if (keyWords.contains(current.toUpperCase())) {
                selection = current.toUpperCase();
                arguments.put(selection, new ArrayList<>());
            } else {
                if (selection == null) {
                    throw new RuntimeException();
                }

                if (!arguments.containsKey(selection)) {
                    arguments.put(selection, Collections.singletonList(current));
                } else {
                    arguments.get(selection).add(current);
                }

            }

        }


        return arguments;
    }

    public String formatString(List<String> strings) {
        String string = strings.get(0);

        for (int i = 1; i < strings.size(); i++) {
            string += " " + strings.get(i);
        }

        return string.replace("'", "");
    }

    public int formatInteger(List<String> strings) {
        String string = strings.get(0);

        for (int i = 1; i < strings.size(); i++) {
            string += " " + strings.get(i);
        }

        return Integer.parseInt(string.replace("'", ""));
    }

}
