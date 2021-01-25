package org.jokergames.jfql.server;

import org.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.server.controller.*;
import org.jokergames.jfql.server.util.ResponseBuilder;
import io.javalin.Javalin;
import org.jokergames.jfql.server.controller.*;
import org.jokergames.jfql.server.util.Method;

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
                    case Method.STATUS:
                        app.error(declarer.status(), context -> controllerService.invokeMethodsByDeclarerAndController(controller, declarer, context));
                        break;
                    case Method.GET:
                        app.get(declarer.path(), context -> controllerService.invokeMethodsByDeclarerAndController(controller, declarer, context));
                        break;
                    case Method.POST:
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
