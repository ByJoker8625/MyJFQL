package org.jokergames.myjfql.util;

import java.util.Arrays;
import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Requirement that = (Requirement) o;
        return Arrays.equals(strings, that.strings) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(type);
        result = 31 * result + Arrays.hashCode(strings);
        return result;
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
