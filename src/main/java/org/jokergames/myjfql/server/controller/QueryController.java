package org.jokergames.myjfql.server.controller;

import io.javalin.http.Context;
import org.jokergames.myjfql.command.executor.RemoteExecutor;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.encryption.Encryption;
import org.jokergames.myjfql.encryption.EncryptionService;
import org.jokergames.myjfql.event.ClientLoginEvent;
import org.jokergames.myjfql.event.CommandExecuteEvent;
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
        final EncryptionService encryptionService = MyJFQL.getInstance().getEncryptionService();

        Encryption.Protocol protocol = null;// = encryptionService.getDefaultEncryption().getProtocol();
        String encryptionKey = null;// encryptionService.getEncryptionKeys().get("default");

        boolean error = false;

        try {
            Encryption encryption = encryptionService.getDefaultEncryption();
            protocol = encryption.getProtocol();
            encryptionKey = encryption.getKey();
        } catch (Exception ex) {
            error = true;
        }
        if (context.queryParamMap().containsKey("protocol")) {
            try {
                Encryption encryption = encryptionService.getEncryption(context.queryParam("protocol"));
                protocol = encryption.getProtocol();
                encryptionKey = encryption.getKey();
            } catch (Exception ex) {
            }
        }

        RemoteExecutor executor = new RemoteExecutor(context.req.getRemoteAddr(), context, protocol, encryptionKey);
        JSONObject jsonObject = new JSONObject();

        if (error) {
            executor.sendError("Unknown protocol error!");
            return;
        }

        {
            final JSONObject request = new RequestReader(context.req).jsonRequest();

            for (String key : request.keySet()) {
                if (key.equals("auth")) {
                    JSONObject subJSONObject = new JSONObject();
                    JSONObject subRequest = request.getJSONObject(key);

                    for (String subKey : subRequest.keySet()) {
                        subJSONObject.put(subKey, protocol.decrypt(subRequest.get(subKey).toString(), encryptionKey));
                    }

                    jsonObject.put(key, subJSONObject);
                } else
                    jsonObject.put(key, request.get(key));
            }
        }

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

            if (user.is(User.Property.STATIC_DATABASE)) {
                MyJFQL.getInstance().getDBSession().put(user.getName(), user.getName());
            }

            MyJFQL.getInstance().getConsole().setInput(false);
            MyJFQL.getInstance().getConsole().logInfo("[" + executor.getName() + "/" + user.getName() + "] queried ('" + jsonObject.getString("query") + "').");
            MyJFQL.getInstance().getConsole().setInput(true);

            String query = jsonObject.getString("query");

            if (query.split(" ").length == 0) {
                executor.sendSyntax();
                return;
            }

            if (MyJFQL.getInstance().getCommandService().getCommand(query.split(" ")[0]) == null) {
                executor.sendSyntax();
                return;
            }

            boolean exec = MyJFQL.getInstance().getCommandService().execute(user, executor, MyJFQL.getInstance().getFormatter().formatCommand(query));
            MyJFQL.getInstance().getEventService().callEvent(CommandExecuteEvent.TYPE, new CommandExecuteEvent(executor, user, query));

            if (!exec)
                executor.sendForbidden();
        } catch (Exception ex) {
            executor.sendError(ex);
        }
    }

}
