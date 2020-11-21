package de.jokergames.jfql.jvl.controller;

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

    public void invokeMethodsByDeclarerAndController(Controller controller, ControllerDeclarer declarer, Context context) {
        final Class<?> clazz = controller.getClass();

        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(ControllerDeclarer.class)) {
                final ControllerDeclarer controllerDeclarer = method.getAnnotation(ControllerDeclarer.class);

                if (controllerDeclarer.method() == declarer.method() && controllerDeclarer.path().equals(declarer.path())) {
                    try {
                        method.invoke(controller, context);
                    } catch (Exception ex) {
                        new NetworkException(ex).printStackTrace();
                    }
                }
            }
        }

    }

    public void invokeMethodByDeclarer(ControllerDeclarer declarer, Context context) {
        controllers.forEach(controller -> invokeMethodsByDeclarerAndController(controller, declarer, context));
    }

    public List<ControllerDeclarer> getControllerDeclarers() {
        final List<ControllerDeclarer> declarers = new ArrayList<>();
        controllers.stream().map(this::getControllerDeclarerByController).forEach(declarers::addAll);
        return declarers;
    }

    public List<ControllerDeclarer> getControllerDeclarerByPath(String path) {
        List<ControllerDeclarer> declarers = new ArrayList<>();

        for (Controller controller : controllers) {
            Class<?> clazz = controller.getClass();

            for (Method method : clazz.getMethods()) {

                if (method.isAnnotationPresent(ControllerDeclarer.class)) {
                    ControllerDeclarer controllerDeclarer = method.getAnnotation(ControllerDeclarer.class);

                    if (controllerDeclarer.path().equals(path)) {
                        declarers.add(controllerDeclarer);
                    }
                }
            }
        }

        return declarers;
    }

    public List<ControllerDeclarer> getControllerDeclarerByController(Controller controller) {
        List<ControllerDeclarer> declarers = new ArrayList<>();

        {
            Class<?> clazz = controller.getClass();

            for (Method current : clazz.getMethods()) {

                if (current.isAnnotationPresent(ControllerDeclarer.class)) {
                    ControllerDeclarer controllerDeclarer = current.getAnnotation(ControllerDeclarer.class);
                    declarers.add(controllerDeclarer);
                }
            }
        }

        return declarers;
    }


    public List<ControllerDeclarer> getControllerDeclarerByMethod(de.jokergames.jfql.jvl.util.Method method) {
        List<ControllerDeclarer> declarers = new ArrayList<>();

        for (Controller controller : controllers) {
            Class<?> clazz = controller.getClass();

            for (Method current : clazz.getMethods()) {

                if (current.isAnnotationPresent(ControllerDeclarer.class)) {
                    ControllerDeclarer controllerDeclarer = current.getAnnotation(ControllerDeclarer.class);

                    if (controllerDeclarer.method() == method) {
                        declarers.add(controllerDeclarer);
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
