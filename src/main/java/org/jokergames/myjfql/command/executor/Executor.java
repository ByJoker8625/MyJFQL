package org.jokergames.myjfql.command.executor;

import java.util.Objects;

/**
 * @author Janick
 */

public abstract class Executor {

    private final String name;

    public Executor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Executor executor = (Executor) o;
        return Objects.equals(name, executor.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Executor{" +
                "name='" + name + '\'' +
                '}';
    }
}
