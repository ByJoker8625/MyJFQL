package de.jokergames.jfql.command.executor;

import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.exception.CommandException;
import de.jokergames.jfql.exception.NetworkException;
import de.jokergames.jfql.jvl.util.ResponseBuilder;
import io.javalin.http.Context;
import org.json.JSONObject;

import java.util.List;

/**
 * @author Janick
 */

public class RemoteExecutor extends Executor {

    private final Context context;
    private final ResponseBuilder builder;

    public RemoteExecutor(String name, Context context) {
        super(name);
        this.context = context;
        this.builder = JFQL.getInstance().getJavalinService().getResponseBuilder();
    }

    public void sendError(String s) {
        send(builder.buildBadMethod(new CommandException(s)));
    }

    public void sendError(Exception e) {
        send(builder.buildBadMethod(new CommandException(e)));
    }

    public void sendSuccess() {
        send(builder.buildSuccess());
    }

    public void sendSyntax() {
        send(builder.buildSyntax());
    }

    public void status(int status) {
        if (context == null)
            return;

        try {
            context.header("Access-Control-Allow-Origin", "*");
            context.header("Access-Control-Allow-Methods", "GET,POST");
            context.header("Access-Control-Allow-Headers", "*");
            context.header("Access-Control-Allow-Credentials", "true");
            context.header("Access-Control-Allow-Credentials-Header", "*");

            context.status(status);
        } catch (Exception ex) {
            throw new NetworkException(ex);
        }
    }

    public void sendForbidden() {
        send(builder.buildForbidden());
    }

    public void sendAnswer(Object object, List<String> strings) {
        send(builder.buildAnswer(object, strings));
    }

    public void sendAnswer(Object object, String[] strings) {
        send(builder.buildAnswer(object, strings));
    }

    private void send(JSONObject response) {
        if (context == null)
            return;

        try {
            context.header("Access-Control-Allow-Origin", "*");
            context.header("Access-Control-Allow-Methods", "GET,POST");
            context.header("Access-Control-Allow-Headers", "*");
            context.header("Access-Control-Allow-Credentials", "true");
            context.header("Access-Control-Allow-Credentials-Header", "*");

            context.result(response.toString()).status(response.getInt("rCode"));
        } catch (Exception ex) {
            throw new NetworkException(ex);
        }
    }

}
