package de.jokergames.jfql.core.boot;

import java.util.Objects;

public class BootArgument {

    private final String name;
    private final String value;

    public BootArgument(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public boolean hasValue() {
        return value != null;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BootArgument that = (BootArgument) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
