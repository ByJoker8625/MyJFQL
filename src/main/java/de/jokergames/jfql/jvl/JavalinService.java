package de.jokergames.jfql.jvl;

import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.jvl.controller.Controller;
import de.jokergames.jfql.jvl.controller.ControllerRegistry;
import de.jokergames.jfql.jvl.controller.QueryController;
import de.jokergames.jfql.jvl.util.ResponseBuilder;
import io.javalin.Javalin;

/**
 * @author Janick
 */

public class JavalinService {

    private final Javalin app;
    private final ResponseBuilder responseBuilder;
    private final ControllerRegistry controllerRegistry;

    public JavalinService() {
        this.responseBuilder = new ResponseBuilder();
        this.controllerRegistry = new ControllerRegistry();
        this.app = Javalin.create();

        controllerRegistry.registerController(new QueryController());
        app.config.showJavalinBanner = false;

        for (Controller controller : controllerRegistry.getControllers()) {
            switch (controller.getMethod()) {
                case GET:
                    app.get(controller.getPath(), controller);
                    break;
                case PUT:
                    app.put(controller.getPath(), controller);
                    break;
                case POST:
                    app.post(controller.getPath(), controller);
                    break;
            }
        }

        app.start(JFQL.getInstance().getConfiguration().getInt("Port"));
    }

    public Javalin getJavalinApp() {
        return app;
    }

    public ControllerRegistry getControllerRegistry() {
        return controllerRegistry;
    }

    public ResponseBuilder getResponseBuilder() {
        return responseBuilder;
    }
}
