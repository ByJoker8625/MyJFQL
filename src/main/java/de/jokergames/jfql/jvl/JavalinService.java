package de.jokergames.jfql.jvl;

import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.jvl.controller.Controller;
import de.jokergames.jfql.jvl.controller.ControllerDeclarer;
import de.jokergames.jfql.jvl.controller.ControllerService;
import de.jokergames.jfql.jvl.controller.QueryController;
import de.jokergames.jfql.jvl.util.ResponseBuilder;
import io.javalin.Javalin;

import java.util.List;

/**
 * @author Janick
 */

public class JavalinService {

    private final Javalin app;
    private final ResponseBuilder responseBuilder;
    private final ControllerService controllerService;

    public JavalinService() {
        this.responseBuilder = new ResponseBuilder();
        this.controllerService = new ControllerService();
        this.app = Javalin.create();

        controllerService.registerController(new QueryController());
        app.config.showJavalinBanner = false;

        for (Controller controller : controllerService.getControllers()) {
            final List<ControllerDeclarer> declarers = controllerService.getControllerDeclarerByController(controller);

            for (ControllerDeclarer declarer : declarers) {
                switch (declarer.method()) {
                    case GET:
                        app.get(declarer.path(), context -> controllerService.invokeMethodsByDeclarerAndController(controller, declarer, context));
                        break;
                    case POST:
                        app.post(declarer.path(), context -> controllerService.invokeMethodsByDeclarerAndController(controller, declarer, context));
                        break;
                }

            }

        /*
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
        }*/

            app.start(JFQL.getInstance().getConfiguration().getInt("Port"));
        }
    }

    public Javalin getJavalinApp() {
        return app;
    }

    public ControllerService getControllerService() {
        return controllerService;
    }

    public ResponseBuilder getResponseBuilder() {
        return responseBuilder;
    }
}
