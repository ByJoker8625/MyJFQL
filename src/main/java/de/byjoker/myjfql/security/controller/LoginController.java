package de.byjoker.myjfql.security.controller;

import de.byjoker.jfql.util.ID;
import de.byjoker.myjfql.command.RestCommandSender;
import de.byjoker.myjfql.config.Config;
import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.user.User;
import de.byjoker.myjfql.user.UserService;
import de.byjoker.myjfql.user.session.Session;
import de.byjoker.myjfql.user.session.SessionService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Collections;

public class LoginController implements Handler {

    private final Config config = MyJFQL.getInstance().getConfig();
    private final UserService userService = MyJFQL.getInstance().getUserService();
    private final SessionService sessionService = MyJFQL.getInstance().getSessionService();

    @Override
    public void handle(@NotNull Context context) throws Exception {
        RestCommandSender sender = new RestCommandSender(null, context);

        try {
            JSONObject request = new JSONObject(context.body());

            if (!request.has("user") || !request.has("password")) {
                sender.sendError("Incomplete request authorization!");
                return;
            }

            final String userIdentifier = request.getString("user");

            if (userIdentifier.equals("%TOKEN%")) {
                final String token = request.getString("password");

                if (!sessionService.existsSession(token)) {
                    sender.sendForbidden();
                    return;
                }

                if (!config.crossTokenRequests() && !sessionService.getSession(token).validAddress(context.ip())) {
                    sender.sendForbidden();
                    return;
                }

                sender.sendResult(Collections.singletonList(token), new String[]{"Token"});
                return;
            }

            if (config.onlyManualSessionControl()) {
                sender.sendForbidden();
                return;
            }

            if (!userService.existsUserByIdentifier(userIdentifier)) {
                sender.sendForbidden();
                return;
            }

            final User user = userService.getUserByIdentifier(userIdentifier);

            if (!user.validPassword(request.getString("password"))) {
                sender.sendForbidden();
                return;
            }

            final String token = ID.generateMixed().toString();
            sessionService.openSession(new Session(token, user.getId(), (user.hasPreferredDatabase()) ? user.getPreferredDatabase() : null, context.ip()));

            if (config.showConnections() && config.showQueries())
                MyJFQL.getInstance().getConsole().logInfo("Client " + context.ip() + " opened a session as '" + user.getName() + "'.");

            sender.sendResult(Collections.singletonList(token), new String[]{"Token"});
        } catch (Exception ex) {
            sender.sendError(ex);
        }
    }

}
