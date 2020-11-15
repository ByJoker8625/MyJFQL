package de.jokergames.jfql.jvl.controller;

import java.util.ArrayList;
import java.util.List;

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

    public List<Controller> getControllers() {
        return controllers;
    }
}
