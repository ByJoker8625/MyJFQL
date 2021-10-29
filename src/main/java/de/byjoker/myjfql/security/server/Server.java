package de.byjoker.myjfql.security.server;

import de.byjoker.myjfql.command.RestCommandSender;
import de.byjoker.myjfql.security.server.handler.LoginHandler;
import de.byjoker.myjfql.security.server.handler.LogoutHandler;
import de.byjoker.myjfql.security.server.handler.QueryEmulatorHandler;
import de.byjoker.myjfql.security.server.handler.QueryHandler;
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
        app.post("api/v1/session/open", new LoginHandler());
        app.post("api/v1/session/close", new LogoutHandler());
        app.post("api/v1/query", new QueryHandler());
        app.post("query", new QueryEmulatorHandler());

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
