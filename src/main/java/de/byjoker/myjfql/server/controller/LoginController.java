package de.byjoker.myjfql.server.controller;

import de.byjoker.myjfql.command.RestCommandSender;
import de.byjoker.myjfql.config.Config;
import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.OneFieldTableEntry;
import de.byjoker.myjfql.server.session.Session;
import de.byjoker.myjfql.server.session.SessionService;
import de.byjoker.myjfql.user.User;
import de.byjoker.myjfql.user.UserService;
import de.byjoker.myjfql.util.IDGenerator;
import de.byjoker.myjfql.util.ResultType;
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

                final Session session = sessionService.getSession(token);

                if (!config.crossTokenRequests() && !session.validAddress(context.ip())) {
                    sender.sendForbidden();
                    return;
                }

                if (config.crossTokenRequests()) {
                    session.setAddress(context.ip());
                    session.utilize();

                    sessionService.saveSession(session);
                }

                if (config.showConnections())
                    MyJFQL.getInstance().getConsole().logInfo("Client " + context.ip() + " joined the session '" + session.getToken() + "'.");

                sender.sendResult(Collections.singletonList(new OneFieldTableEntry("token", token)), Collections.singletonList("token"), ResultType.RELATIONAL);
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

            final String token = IDGenerator.generateMixed(25);
            sessionService.openSession(new Session(token, user.getId(), (user.hasPreferredDatabase()) ? user.getPreferredDatabase() : null, context.ip()));

            if (config.showConnections())
                MyJFQL.getInstance().getConsole().logInfo("Client " + context.ip() + " opened a session as '" + user.getName() + "'.");

            sender.sendResult(Collections.singletonList(new OneFieldTableEntry("token", token)), Collections.singletonList("token"), ResultType.RELATIONAL);
        } catch (Exception ex) {
            sender.sendError(ex);
        }
    }

}
