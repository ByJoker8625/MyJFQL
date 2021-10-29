package de.byjoker.myjfql.security.server.handler;

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

public class LoginHandler implements Handler {

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

            if (!userService.existsUserByIdentifier(request.getString("user"))) {
                sender.sendForbidden();
                return;
            }

            final User user = userService.getUserByIdentifier(request.getString("user"));

            if (!user.validPassword(request.getString("password"))) {
                sender.sendForbidden();
                return;
            }

            final String token = ID.generateMixed().toString();
            sessionService.openSession(new Session(token, user.getId(), (user.hasPreferredDatabase()) ? user.getPreferredDatabase() : null, context.req.getRemoteAddr()));

            if (config.showConnections() && config.showQueries())
                MyJFQL.getInstance().getConsole().logInfo("Client " + context.ip() + " opened a session.");

            sender.sendResult(Collections.singletonList(token), new String[]{"Token"});
        } catch (Exception ex) {
            sender.sendError(ex);
        }
    }

}
