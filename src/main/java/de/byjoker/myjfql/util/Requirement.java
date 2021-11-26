package de.byjoker.myjfql.util;

import java.util.Arrays;

public class Requirement {

    private final String[] strings;
    private final Type type;

    public Requirement(final String[] strings, final Type type) {
        this.strings = strings;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Requirement{" +
                "strings=" + Arrays.toString(strings) +
                ", type=" + type +
                '}';
    }

    public String[] getStrings() {
        return strings;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        NEGATIVE,
        POSITIVE
    }
}
