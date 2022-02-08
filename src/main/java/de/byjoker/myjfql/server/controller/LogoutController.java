package de.byjoker.myjfql.server.controller;

import de.byjoker.myjfql.command.ContextCommandSender;
import de.byjoker.myjfql.config.Config;
import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.server.session.SessionService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class LogoutController implements Handler {

    private final Config config = MyJFQL.getInstance().getConfig();
    private final SessionService sessionService = MyJFQL.getInstance().getSessionService();

    @Override
    public void handle(@NotNull Context context) throws Exception {
        ContextCommandSender sender = new ContextCommandSender(null, context);

        try {
            final JSONObject request = new JSONObject(context.body());

            if (!request.has("token")) {
                sender.sendError("Incomplete request authorization!");
                return;
            }

            final String token = request.getString("token");

            if (!sessionService.existsSession(token)) {
                sender.sendForbidden();
                return;
            }

            if (!config.crossTokenRequests() && !sessionService.getSession(token).validAddress(context.ip())) {
                sender.sendForbidden();
                return;
            }

            if (config.showConnections())
                MyJFQL.getInstance().getConsole().logInfo("User '" + sender.getName() + "' from " + context.ip() + " closes his session.");

            sessionService.closeSession(token);
            sender.sendSuccess();
        } catch (Exception ex) {
            sender.sendError(ex);
        }
    }
}
