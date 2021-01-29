package org.jokergames.jfql.server;

import io.javalin.Javalin;
import org.jokergames.jfql.core.JFQL;
import org.jokergames.jfql.server.controller.*;
import org.jokergames.jfql.server.util.ResponseBuilder;

import java.util.List;

/**
 * @author Janick
 */

public class Server {

    private final Javalin app;
    private final ResponseBuilder responseBuilder;
    private final ControllerService controllerService;

    public Server() {
        this.responseBuilder = new ResponseBuilder();
        this.controllerService = new ControllerService();
        this.app = Javalin.create();

        controllerService.registerController(new QueryController());
        controllerService.registerController(new ErrorController());
        app.config.showJavalinBanner = false;

        app.start(JFQL.getInstance().getConfiguration().getInt("Port"));

        for (Controller controller : controllerService.getControllers()) {
            final List<ControllerHandler> declarers = controllerService.getControllerDeclarerByController(controller);

            for (ControllerHandler declarer : declarers) {
                switch (declarer.method()) {
                    case STATUS:
                        app.error(declarer.status(), context -> controllerService.invokeMethodsByDeclarerAndController(controller, declarer, context));
                        break;
                    case GET:
                        app.get(declarer.path(), context -> controllerService.invokeMethodsByDeclarerAndController(controller, declarer, context));
                        break;
                    case POST:
                        app.post(declarer.path(), context -> controllerService.invokeMethodsByDeclarerAndController(controller, declarer, context));
                        break;
                }

            }

        }
    }

    @Deprecated
    public Javalin getApp() {
        return app;
    }

    public ControllerService getControllerService() {
        return controllerService;
    }

    public ResponseBuilder getResponseBuilder() {
        return responseBuilder;
    }
}
