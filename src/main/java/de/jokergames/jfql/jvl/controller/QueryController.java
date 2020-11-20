package de.jokergames.jfql.jvl.controller;

import de.jokergames.jfql.command.executor.RemoteExecutor;
import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.event.ClientLoginEvent;
import de.jokergames.jfql.jvl.util.Method;
import de.jokergames.jfql.jvl.util.RequestReader;
import de.jokergames.jfql.jvl.util.ResponseBuilder;
import de.jokergames.jfql.user.User;
import de.jokergames.jfql.user.UserHandler;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

/**
 * @author Janick
 */

public class QueryController extends Controller {

    public QueryController() {
        super("/query", Method.POST);
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        final UserHandler userHandler = JFQL.getInstance().getUserHandler();
        final ResponseBuilder builder = JFQL.getInstance().getJavalinService().getResponseBuilder();

        final JSONObject jsonObject = new RequestReader(context.req).jsonRequest();
        final RemoteExecutor executor = new RemoteExecutor(context.req.getRemoteAddr(), context);

        try {
            User user;

            {
                JSONObject auth = jsonObject.getJSONObject("auth");

                if (userHandler.getUser(auth.getString("user")) == null) {
                    JFQL.getInstance().getEventService().callEvent(ClientLoginEvent.TYPE, new ClientLoginEvent(executor, false));
                    executor.send(builder.buildForbidden());
                    return;
                }

                user = userHandler.getUser(auth.getString("user"));

                if (user.is(User.Property.CONSOLE)) {
                    JFQL.getInstance().getEventService().callEvent(ClientLoginEvent.TYPE, new ClientLoginEvent(executor, false));
                    executor.send(builder.buildForbidden());
                    return;
                }

                if (!user.getPassword().equals(auth.getString("password"))) {
                    JFQL.getInstance().getEventService().callEvent(ClientLoginEvent.TYPE, new ClientLoginEvent(executor, false));
                    executor.send(builder.buildForbidden());
                    return;
                }

            }

            if (jsonObject.getString("query").equals("#connect")) {
                executor.sendError(200);
                return;
            }

            JFQL.getInstance().getEventService().callEvent(ClientLoginEvent.TYPE, new ClientLoginEvent(executor, true));
            JFQL.getInstance().getConsole().logInfo("[" + executor.getName() + "] ordered [\"" + jsonObject.getString("query") + "\"].");

            boolean exec = JFQL.getInstance().getCommandService().execute(user, executor, JFQL.getInstance().getFormatter().formatCommand(jsonObject.getString("query")));

            if (!exec) {
                executor.send(builder.buildForbidden());
            }

        } catch (Exception ex) {
            executor.send(builder.buildBadMethod(ex));
        }
    }

}
