package de.jokergames.jfql.util;

import java.util.Arrays;

/**
 * @author Janick
 */

public class Requirement {

    private final String[] strings;
    private final Type type;

    public Requirement(String[] strings, Type type) {
        this.strings = strings;
        this.type = type;
    }

    public String[] getStrings() {
        return strings;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Requirement{" +
                "strings=" + Arrays.toString(strings) +
                ", type=" + type +
                '}';
    }

    public enum Type {
        POSITIVE,
        NEGATIVE,
        BIGGER,
        SMALLER
    }
}
