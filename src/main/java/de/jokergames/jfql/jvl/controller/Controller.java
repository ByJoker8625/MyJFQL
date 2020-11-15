package de.jokergames.jfql.jvl.controller;

import de.jokergames.jfql.jvl.util.Method;
import io.javalin.http.Handler;

/**
 * @author Janick
 */

public abstract class Controller implements Handler {

    private final String path;
    private final Method method;

    public Controller(String path, Method method) {
        this.path = path;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public Method getMethod() {
        return method;
    }
}
