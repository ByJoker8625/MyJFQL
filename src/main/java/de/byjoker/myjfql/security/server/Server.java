package de.byjoker.myjfql.security.server;

import de.byjoker.myjfql.command.RestCommandSender;
import de.byjoker.myjfql.security.server.handler.LoginHandler;
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

        /*
        final Console console = MyJFQL.getInstance().getConsole();
        final CommandService commandService = MyJFQL.getInstance().getCommandService();
        final UserService userService = MyJFQL.getInstance().getUserService();
*/
        app.error(404, context -> context.result(new JSONObject().put("type", RestCommandSender.ResponseType.SYNTAX_ERROR).toString()));
        app.post("api/v1/login", new LoginHandler());
        app.post("api/v1/query", new QueryHandler());
/*
        {
            final Config config = MyJFQL.getInstance().getConfig();

            app.post("/query", context -> {
                RemoteCommandSender sender = new RemoteCommandSender(null, context.req.getRemoteAddr(), context);

                try {
                    final JSONObject request = new JSONObject(context.body());

                    if (!request.isNull("auth")) {
                        final JSONObject auth = request.getJSONObject("auth");
                        request.put("name", auth.getString("user"));
                        request.put("password", auth.getString("password"));
                        request.remove("auth");
                    }

                    // TODO: 28.10.2021 login sessions with tokens

                    final String name = request.getString("name");
                    final String password = MyJFQL.getInstance().getEncryptor().encrypt(request.getString("password"));

                    if (userService.existsUserByName(name) && userService.getUserByName(name).getPassword().equals(password)) {
                        final String query = request.getString("query");
                        sender = sender.rename(name);

                        if (query.equals("#connect") && !config.showConnections()) {
                            commandService.execute(sender, query);
                            return;
                        }

                        if (config.showQueries())
                            console.logInfo("[" + sender.getAddress() + "] [" + name + "] queried '" + query + "'.");
                        commandService.execute(sender, query);
                        return;
                    }

                    sender.sendForbidden();
                } catch (Exception ex) {
                    sender.sendError(ex);
                }
            });
        }

 */

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
