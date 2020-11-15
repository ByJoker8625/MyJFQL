package de.jokergames.jfql.jvl;

import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.jvl.controller.Controller;
import de.jokergames.jfql.jvl.controller.QueryController;
import de.jokergames.jfql.jvl.util.ResponseBuilder;
import io.javalin.Javalin;

/**
 * @author Janick
 */

public class JavalinService {

    private final Javalin app;
    private final ResponseBuilder responseBuilder;

    public JavalinService() {
        this.app = Javalin.create();
        this.responseBuilder = new ResponseBuilder();
        app.config.showJavalinBanner = false;

        JFQL.getInstance().getControllerRegistry().registerController(new QueryController());

        for (Controller controller : JFQL.getInstance().getControllerRegistry().getControllers()) {
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

    public Javalin getApp() {
        return app;
    }

    public ResponseBuilder getResponseBuilder() {
        return responseBuilder;
    }
}
