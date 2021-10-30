package de.byjoker.myjfql.security.controller;

import de.byjoker.myjfql.command.RestCommandSender;
import de.byjoker.myjfql.config.Config;
import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.user.session.SessionService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class LogoutController implements Handler {

    private final Config config = MyJFQL.getInstance().getConfig();
    private final SessionService sessionService = MyJFQL.getInstance().getSessionService();

    @Override
    public void handle(@NotNull Context context) throws Exception {
        RestCommandSender sender = new RestCommandSender(null, context);

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

            sessionService.closeSession(token);
            sender.sendSuccess();
        } catch (Exception ex) {
            sender.sendError(ex);
        }
    }
}
