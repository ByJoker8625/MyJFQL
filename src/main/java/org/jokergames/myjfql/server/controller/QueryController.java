package org.jokergames.myjfql.server.controller;

import io.javalin.http.Context;
import org.jokergames.myjfql.command.executor.RemoteExecutor;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.event.ClientLoginEvent;
import org.jokergames.myjfql.server.util.Method;
import org.jokergames.myjfql.server.util.RequestReader;
import org.jokergames.myjfql.user.User;
import org.jokergames.myjfql.user.UserService;
import org.json.JSONObject;

/**
 * @author Janick
 */

public class QueryController implements Controller {

    @ControllerHandler(path = "/query", method = Method.POST)
    public void handleQuery(Context context) throws Exception {
        final UserService userService = MyJFQL.getInstance().getUserService();

        final JSONObject jsonObject = new RequestReader(context.req).jsonRequest();
        final RemoteExecutor executor = new RemoteExecutor(context.req.getRemoteAddr(), context);

        try {
            User user;

            {
                JSONObject auth = jsonObject.getJSONObject("auth");

                if (userService.getUser(auth.getString("user")) == null) {
                    MyJFQL.getInstance().getEventService().callEvent(ClientLoginEvent.TYPE, new ClientLoginEvent(executor, false));
                    executor.sendForbidden();
                    return;
                }

                user = userService.getUser(auth.getString("user"));

                if (user.is(User.Property.CONSOLE)) {
                    MyJFQL.getInstance().getEventService().callEvent(ClientLoginEvent.TYPE, new ClientLoginEvent(executor, false));
                    executor.sendForbidden();
                    return;
                }

                if (!user.getPassword().equals(auth.getString("password"))) {
                    MyJFQL.getInstance().getEventService().callEvent(ClientLoginEvent.TYPE, new ClientLoginEvent(executor, false));
                    executor.sendForbidden();
                    return;
                }

            }

            if (jsonObject.getString("query").equals("#connect")) {
                executor.status(200);
                return;
            }

            MyJFQL.getInstance().getEventService().callEvent(ClientLoginEvent.TYPE, new ClientLoginEvent(executor, true));

            MyJFQL.getInstance().getConsole().setInput(false);
            MyJFQL.getInstance().getConsole().logInfo("[" + executor.getName() + "/" + user.getName() + "] queried ('" + jsonObject.getString("query") + "').");
            MyJFQL.getInstance().getConsole().setInput(true);

            boolean exec = MyJFQL.getInstance().getCommandService().execute(user, executor, MyJFQL.getInstance().getFormatter().formatCommand(jsonObject.getString("query")));

            if (!exec)
                executor.sendForbidden();

        } catch (Exception ex) {
            executor.sendError(ex);
        }
    }

}
