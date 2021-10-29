package de.byjoker.myjfql.security.server.handler;

import de.byjoker.myjfql.command.RestCommandSender;
import de.byjoker.myjfql.config.Config;
import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.user.session.Session;
import de.byjoker.myjfql.user.session.SessionService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class QueryHandler implements Handler {

    private final Config config = MyJFQL.getInstance().getConfig();
    private final SessionService sessionService = MyJFQL.getInstance().getSessionService();

    @Override
    public void handle(@NotNull Context context) throws Exception {
        RestCommandSender sender = new RestCommandSender(null, context);

        try {
            final JSONObject request = new JSONObject(context.body());

            if (!request.has("query") || !request.has("token")) {
                sender.sendError("Incomplete request authorization!");
                return;
            }

            final String token = request.getString("token");

            if (!sessionService.existsSession(token)) {
                sender.sendForbidden();
                return;
            }

            final Session session = sessionService.getSession(token);
            session.setAddress(context.req.getRemoteAddr());
            session.utilize();

            sender = sender.bind(session);
            sessionService.saveSession(session);

            final String query = request.getString("query");

            if (config.showQueries())
                MyJFQL.getInstance().getConsole().logInfo("User '" + sender.getName() + "' from " + session.getAddress() + " queried '" + query + "'.");

            MyJFQL.getInstance().getCommandService().execute(sender, query);
        } catch (Exception ex) {
            sender.sendError(ex);
        }
    }

}
