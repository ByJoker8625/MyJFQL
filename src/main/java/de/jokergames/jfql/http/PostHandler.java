package de.jokergames.jfql.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.jokergames.jfql.command.executor.RemoteExecutor;
import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.http.util.Builder;
import de.jokergames.jfql.user.User;
import de.jokergames.jfql.user.UserHandler;
import org.json.JSONObject;

import java.io.IOException;

/**
 * @author Janick
 */

public class PostHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final UserHandler userHandler = JFQL.getInstance().getUserHandler();
        final Builder outputBuilder = JFQL.getInstance().getBuilder();

        final var inputStream = exchange.getRequestBody();
        final var builder = new StringBuilder();

        int read;

        while ((read = inputStream.read()) != -1) {
            builder.append((char) read);
        }

        inputStream.close();

        final JSONObject jsonObject = new JSONObject(builder.toString());
        final RemoteExecutor executor = new RemoteExecutor(exchange.getRemoteAddress().getHostName(), exchange);

        try {

            User user;

            {
                JSONObject auth = jsonObject.getJSONObject("auth");

                if (userHandler.getUser(auth.getString("user")) == null) {
                    executor.send(outputBuilder.buildForbidden());
                    return;
                }

                user = userHandler.getUser(auth.getString("user"));

                if (user.is(User.Property.CONSOLE)) {
                    executor.send(outputBuilder.buildForbidden());
                    return;
                }

                if (!user.getPassword().equals(auth.getString("password"))) {
                    executor.send(outputBuilder.buildForbidden());
                    return;
                }

            }

            if (jsonObject.getString("query").equals("#connect")) {
                executor.sendError(200);
                return;
            }

            boolean exec = JFQL.getInstance().getCommandHandler().execute(user, executor, JFQL.getInstance().getFormatter().formatCommand(jsonObject.getString("query")));

            if (!exec) {
                executor.send(outputBuilder.buildForbidden());
            }

        } catch (Exception ex) {
            executor.send(JFQL.getInstance().getBuilder().buildBadMethod(ex));
        }

    }

}
