package org.jokergames.jfql.command.executor;

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

}
