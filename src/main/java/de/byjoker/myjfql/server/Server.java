package de.byjoker.myjfql.server;

import de.byjoker.myjfql.command.RestCommandSender;
import de.byjoker.myjfql.server.controller.LoginController;
import de.byjoker.myjfql.server.controller.LogoutController;
import de.byjoker.myjfql.server.controller.QueryController;
import io.javalin.Javalin;
import org.json.JSONObject;

public class Server {

    private Javalin app;
    private int port;

    public void start(int port) {
        this.app = Javalin.create();
        this.port = port;

        app._conf.showJavalinBanner = false;

        app.error(404, context -> context.result(new JSONObject().put("type", RestCommandSender.ResponseType.SYNTAX_ERROR).toString()));
        app.post("api/v1/session/open", new LoginController());
        app.post("api/v1/login", new LoginController());
        app.post("api/v1/session/close", new LogoutController());
        app.post("api/v1/logout", new LogoutController());
        app.post("api/v1/query", new QueryController());

        app.start(port);
    }

    public void shutdown() {
        app.stop();
    }

    public Javalin getApp() {
        return app;
    }

    public int getPort() {
        return port;
    }
}
