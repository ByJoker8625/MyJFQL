package de.byjoker.myjfql.server.controller;

import de.byjoker.myjfql.command.RestCommandSender;
import de.byjoker.myjfql.config.Config;
import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.server.session.Session;
import de.byjoker.myjfql.server.session.SessionService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class QueryController implements Handler {

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

            if (!config.crossTokenRequests() && !session.validAddress(context.ip())) {
                sender.sendForbidden();
                return;
            }

            if (config.crossTokenRequests())
                session.setAddress(context.ip());

            session.utilize();

            sender = sender.bind(session);
            sessionService.saveSession(session);

            final String query = request.getString("query");

            if (config.showQueries())
                MyJFQL.getInstance().getConsole().logInfo("User '" + sender.getName() + "' from " + context.ip() + " queried '" + query + "'.");

            MyJFQL.getInstance().getCommandService().execute(sender, query);
        } catch (Exception ex) {
            sender.sendError(ex);
        }
    }

}
