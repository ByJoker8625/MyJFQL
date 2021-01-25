package org.jokergames.jfql.core.script;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Janick
 */

public class Script {

    private final String name;
    private List<String> commands;

    public Script(String name) {
        this.name = name;
        this.commands = new ArrayList<>();
    }

    public Script(String name, String... commands) {
        this.name = name;
        this.commands = Arrays.asList(commands);
    }

    public String getName() {
        return name;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public void formatCommands(String s) {
        setCommands(Arrays.asList(s.replace("; ", ";").split(";")));
    }

    public File getFile() {
        return new File("script/" + name + ".json");
    }

    @Override
    public String toString() {
        String s = "Script: \"" + name + "\" {\n";

        for (String command : commands) {
            s += ": " + command + "\n";
        }

        s += "}";

        return s;
    }
}
