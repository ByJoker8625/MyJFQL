package org.jokergames.myjfql.server;

import io.javalin.Javalin;
import org.jokergames.myjfql.command.CommandService;
import org.jokergames.myjfql.command.RemoteCommandSender;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.user.UserService;
import org.jokergames.myjfql.util.Console;
import org.json.JSONObject;

import java.util.*;

public class Server {

    private final Javalin app;

    private final List<String> clients;
    private final Map<String, String> confirmedClients;

    public Server(final int port) {
        this.app = Javalin.create();
        app.config.showJavalinBanner = false;

        final Console console = MyJFQL.getInstance().getConsole();
        final CommandService commandService = MyJFQL.getInstance().getCommandService();
        final UserService userService = MyJFQL.getInstance().getUserService();

        this.clients = new ArrayList<>();
        this.confirmedClients = new HashMap<>();

        app.ws("/query", handler -> {

            handler.onConnect(context -> {
                final String sessionID = context.getSessionId();
                clients.add(sessionID);

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!confirmedClients.containsKey(sessionID)) {
                            clients.remove(sessionID);
                            context.session.close();
                        }
                    }
                }, 1000 * 10);
            });

            handler.onMessage(context -> {
                final String sessionID = context.getSessionId();

                if (!confirmedClients.containsKey(sessionID)) {
                    final RemoteCommandSender sender = new RemoteCommandSender(null, context.session.getRemoteAddress().getHostString(), context);

                    try {
                        final JSONObject jsonObject = new JSONObject(context.message());
                        final String name = jsonObject.getString("user");

                        if (userService.isCreated(name) && userService.getUser(name).getPassword().equals(jsonObject.getString("password"))) {
                            confirmedClients.put(sessionID, name);
                            clients.remove(sessionID);
                            sender.sendSuccess();

                            console.setInput(false);
                            console.logInfo("[" + sender.getAddress() + "/" + name + "] oped a connection.");
                            console.setInput(true);
                            return;
                        }

                        sender.sendForbidden();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    return;
                }

                final RemoteCommandSender sender = new RemoteCommandSender(confirmedClients.get(sessionID), context.session.getRemoteAddress().getHostString(), context);

                try {
                    final JSONObject request = new JSONObject(context.message());
                    final int id = request.getInt("id");
                    final String query = request.getString("query");

                    commandService.execute(sender.toCommandSenderWithId(id), query);

                    console.setInput(false);
                    console.logInfo("[" + sender.getAddress() + "/" + sender.getName() + "] queried \"" + query + "\".");
                    console.setInput(true);
                } catch (Exception ex) {
                    sender.sendError(ex);
                }
            });

            handler.onClose(context -> {
                final String sessionID = context.getSessionId();

                if (!confirmedClients.containsKey(sessionID)) {
                    clients.remove(sessionID);
                    return;
                }

                {
                    console.setInput(false);
                    console.logInfo("[" + context.session.getRemoteAddress().getAddress().getHostAddress() + "/" + confirmedClients.get(sessionID) + "] closed the connection.");
                    console.setInput(true);
                }

                confirmedClients.remove(sessionID);
            });

        });

        app.start(port);
    }

    public Map<String, String> getConfirmedClients() {
        return confirmedClients;
    }

    public List<String> getClients() {
        return clients;
    }

    public Javalin getApp() {
        return app;
    }
}
