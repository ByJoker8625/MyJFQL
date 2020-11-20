package de.jokergames.jfql.jvl.controller;

import de.jokergames.jfql.jvl.util.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Janick
 */

public class ControllerRegistry {

    private final List<Controller> controllers;

    public ControllerRegistry() {
        this.controllers = new ArrayList<>();
    }

    public void registerController(Controller controller) {
        controllers.add(controller);
    }

    public void unregisterController(Controller controller) {
        controllers.remove(controller);
    }

    public List<Controller> getControllersByMethod(Method method) {
        return this.controllers.stream().filter(controller -> controller.getMethod() == method).collect(Collectors.toList());
    }

    public List<Controller> getControllerByPath(String path) {
        return this.controllers.stream().filter(controller -> controller.getPath().equalsIgnoreCase(path)).collect(Collectors.toList());
    }

    public List<Controller> getControllers() {
        return controllers;
    }
}
