package org.jokergames.myjfql.server.controller;

import io.javalin.http.Context;
import org.jokergames.myjfql.exception.NetworkException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        Arrays.stream(controller.getClass().getMethods()).filter(method -> method.isAnnotationPresent(ControllerHandler.class)).forEach(method -> {
            final ControllerHandler controllerHandler = method.getAnnotation(ControllerHandler.class);
            if (controllerHandler.method() == declarer.method() && controllerHandler.path().equals(declarer.path())) {
                try {
                    method.invoke(controller, context);
                } catch (Exception ex) {
                    new NetworkException(ex).printStackTrace();
                }
            }
        });
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
        return controllers.stream().map(Controller::getClass).flatMap(clazz -> Arrays.stream(clazz.getMethods())).filter(method -> method.isAnnotationPresent(ControllerHandler.class)).map(method -> method.getAnnotation(ControllerHandler.class)).filter(controllerHandler -> controllerHandler.path().equals(path)).collect(Collectors.toList());
    }

    public List<ControllerHandler> getControllerDeclarerByController(Controller controller) {
        return Arrays.stream(controller.getClass().getMethods()).filter(current -> current.isAnnotationPresent(ControllerHandler.class)).map(current -> current.getAnnotation(ControllerHandler.class)).collect(Collectors.toList());
    }


    public List<ControllerHandler> getControllerDeclarerByMethod(org.jokergames.myjfql.server.util.Method method) {
        return controllers.stream().map(Controller::getClass).flatMap(clazz -> Arrays.stream(clazz.getMethods())).filter(current -> current.isAnnotationPresent(ControllerHandler.class)).map(current -> current.getAnnotation(ControllerHandler.class)).filter(controllerHandler -> controllerHandler.method() == method).collect(Collectors.toList());
    }

    public List<Controller> getControllers() {
        return controllers;
    }
}
