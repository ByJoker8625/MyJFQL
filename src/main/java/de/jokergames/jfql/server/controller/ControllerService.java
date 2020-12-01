package de.jokergames.jfql.server.controller;

import de.jokergames.jfql.exception.NetworkException;
import io.javalin.http.Context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Janick
 */

public class ControllerService {

    private final List<Controller> controllers;

    public ControllerService() {
        this.controllers = new ArrayList<>();
    }

    public void registerController(Controller controller) {
        controllers.add(controller);
    }

    public void unregisterController(Controller controller) {
        controllers.remove(controller);
    }

    public void invokeMethodsByDeclarerAndController(Controller controller, ControllerHandler declarer, Context context) {
        final Class<?> clazz = controller.getClass();

        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(ControllerHandler.class)) {
                final ControllerHandler controllerHandler = method.getAnnotation(ControllerHandler.class);

                if (controllerHandler.method() == declarer.method() && controllerHandler.path().equals(declarer.path())) {
                    try {
                        method.invoke(controller, context);
                    } catch (Exception ex) {
                        new NetworkException(ex).printStackTrace();
                    }
                }
            }
        }

    }

    public void invokeMethodByDeclarer(ControllerHandler declarer, Context context) {
        controllers.forEach(controller -> invokeMethodsByDeclarerAndController(controller, declarer, context));
    }

    public List<ControllerHandler> getControllerDeclarers() {
        final List<ControllerHandler> declarers = new ArrayList<>();
        controllers.stream().map(this::getControllerDeclarerByController).forEach(declarers::addAll);
        return declarers;
    }

    public List<ControllerHandler> getControllerDeclarerByPath(String path) {
        List<ControllerHandler> declarers = new ArrayList<>();

        for (Controller controller : controllers) {
            Class<?> clazz = controller.getClass();

            for (Method method : clazz.getMethods()) {

                if (method.isAnnotationPresent(ControllerHandler.class)) {
                    ControllerHandler controllerHandler = method.getAnnotation(ControllerHandler.class);

                    if (controllerHandler.path().equals(path)) {
                        declarers.add(controllerHandler);
                    }
                }
            }
        }

        return declarers;
    }

    public List<ControllerHandler> getControllerDeclarerByController(Controller controller) {
        List<ControllerHandler> declarers = new ArrayList<>();

        {
            Class<?> clazz = controller.getClass();

            for (Method current : clazz.getMethods()) {

                if (current.isAnnotationPresent(ControllerHandler.class)) {
                    ControllerHandler controllerHandler = current.getAnnotation(ControllerHandler.class);
                    declarers.add(controllerHandler);
                }
            }
        }

        return declarers;
    }


    public List<ControllerHandler> getControllerDeclarerByMethod(de.jokergames.jfql.server.util.Method method) {
        List<ControllerHandler> declarers = new ArrayList<>();

        for (Controller controller : controllers) {
            Class<?> clazz = controller.getClass();

            for (Method current : clazz.getMethods()) {

                if (current.isAnnotationPresent(ControllerHandler.class)) {
                    ControllerHandler controllerHandler = current.getAnnotation(ControllerHandler.class);

                    if (controllerHandler.method() == method) {
                        declarers.add(controllerHandler);
                    }
                }
            }
        }

        return declarers;
    }

    public List<Controller> getControllers() {
        return controllers;
    }
}
