package de.byjoker.myjfql.server;

import de.byjoker.myjfql.command.CommandService;
import de.byjoker.myjfql.command.RemoteCommandSender;
import de.byjoker.myjfql.console.Console;
import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.user.UserService;
import io.javalin.Javalin;
import org.json.JSONObject;

public class Server {

    private Javalin app;
    private int port;

    public void start(int port) {
        this.app = Javalin.create();
        this.port = port;

        app._conf.showJavalinBanner = false;

        final Console console = MyJFQL.getInstance().getConsole();
        final CommandService commandService = MyJFQL.getInstance().getCommandService();
        final UserService userService = MyJFQL.getInstance().getUserService();

        app.error(404, context -> context.result(new JSONObject().put("type", RemoteCommandSender.ResponseType.SYNTAX_ERROR).toString()));

        boolean showConnectionPacket = MyJFQL.getInstance().getConfiguration().showConnectionPacket();

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

                final String name = request.getString("name");

                if (userService.existsUser(name) && userService.getUser(name).getPassword().equals(request.getString("password"))) {
                    final String query = request.getString("query");
                    sender = sender.rename(name);

                    if (query.equals("#connect") && !showConnectionPacket) {
                        commandService.execute(sender, query);
                        return;
                    }

                    console.logInfo("[" + sender.getAddress() + "] [" + name + "] queried \"" + query + "\".");
                    commandService.execute(sender, query);
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
