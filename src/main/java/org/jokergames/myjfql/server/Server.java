package org.jokergames.myjfql.server;

import io.javalin.Javalin;
import org.jokergames.myjfql.command.CommandService;
import org.jokergames.myjfql.command.RemoteCommandSender;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.user.UserService;
import org.jokergames.myjfql.util.Console;
import org.json.JSONObject;

public class Server {

    private Javalin app;
    private int port;

    public void start(int port) {
        this.app = Javalin.create();
        this.port = port;

        app.config.showJavalinBanner = false;

        final Console console = MyJFQL.getInstance().getConsole();
        final CommandService commandService = MyJFQL.getInstance().getCommandService();
        final UserService userService = MyJFQL.getInstance().getUserService();

        app.post("/query", context -> {
            RemoteCommandSender sender = new RemoteCommandSender(null, context.req.getRemoteAddr(), null, context);

            try {
                final JSONObject request = new JSONObject(context.body());

                if (!request.isNull("auth")) {
                    final JSONObject auth = request.getJSONObject("auth");
                    request.put("name", auth.getString("user"));
                    request.put("password", auth.getString("password"));
                    request.remove("auth");
                }

                final String name = request.getString("name");

                if (userService.isCreated(name) && userService.getUser(name).getPassword().equals(request.getString("password"))) {
                    sender = new RemoteCommandSender(name, context.req.getRemoteAddr(), null, context);
                    final String query = request.getString("query");

                    commandService.execute(sender, query);

                    {
                        console.setInput(false);
                        console.logInfo("[" + sender.getAddress() + "] [" + name + "] queried \"" + query + "\".");
                        console.setInput(true);
                    }
                    return;
                }

                sender.sendForbidden();
            } catch (Exception ex) {
                sender.sendError(ex);
            }
        });


        app.start(port);
    }

    public void shutdown() {
        app.stop();
    }

    public void restart() {
        shutdown();
        start(port);
    }

    public Javalin getApp() {
        return app;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
