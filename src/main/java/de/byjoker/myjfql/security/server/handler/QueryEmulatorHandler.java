package de.byjoker.myjfql.security.server.handler;

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

public class QueryEmulatorHandler implements Handler {

    private final Config config = MyJFQL.getInstance().getConfig();
    private final UserService userService = MyJFQL.getInstance().getUserService();
    private final SessionService sessionService = MyJFQL.getInstance().getSessionService();

    @Override
    public void handle(@NotNull Context context) throws Exception {
        RestCommandSender sender = new RestCommandSender(null, context);

        try {
            final JSONObject request = new JSONObject(context.body());

            if (request.has("auth")) {
                request.put("name", request.getJSONObject("auth").getString("user"));
                request.put("password", request.getJSONObject("auth").getString("password"));
            }

            if (!request.has("name") || !request.has("password") || !request.has("query")) {
                sender.sendError("Incomplete request authorization!");
                return;
            }

            final String name = request.getString("name");
            final String password = request.getString("password");

            if (!userService.existsUserByIdentifier(name)) {
                sender.sendForbidden();
                return;
            }

            final User user = userService.getUserByIdentifier(name);

            if (!user.validPassword(password)) {
                sender.sendForbidden();
                return;
            }

            final String token = user.getId() + "." + user.getPassword();

            if (!sessionService.existsSession(token)) {
                sessionService.openSession(new Session(token, user.getId(), (user.hasPreferredDatabase()) ? user.getPreferredDatabase() : null, context.req.getRemoteAddr()));
            }

            final Session session = sessionService.getSession(token);
            sender = sender.bind(session);

            final String query = request.getString("query");

            if (config.showQueries())
                MyJFQL.getInstance().getConsole().logInfo("User '" + sender.getName() + "' from " + session.getAddress() + " queried '" + query + "'.");

            MyJFQL.getInstance().getCommandService().execute(sender, query);
        } catch (Exception ex) {
            sender.sendError(ex);
        }
    }

}
