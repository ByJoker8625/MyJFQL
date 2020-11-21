package de.jokergames.jfql.command.executor;

import de.jokergames.jfql.exception.NetworkException;
import io.javalin.http.Context;
import org.json.JSONObject;

/**
 * @author Janick
 */

public class RemoteExecutor extends Executor {

    private final Context context;

    public RemoteExecutor(String name, Context context) {
        super(name);
        this.context = context;
    }

    public void sendInfo(JSONObject response) {
        if (context == null)
            return;

        try {
            context.header("Access-Control-Allow-Origin", "*");
            context.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            context.header("Access-Control-Allow-Headers", "*");
            context.header("Access-Control-Allow-Credentials", "true");
            context.header("Access-Control-Allow-Credentials-Header", "*");

            context.result(response.toString()).status(200);
        } catch (Exception ex) {
            throw new NetworkException(ex);
        }
    }

    public void send(JSONObject response) {
        if (context == null)
            return;

        try {
            context.header("Access-Control-Allow-Origin", "*");
            context.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            context.header("Access-Control-Allow-Headers", "*");
            context.header("Access-Control-Allow-Credentials", "true");
            context.header("Access-Control-Allow-Credentials-Header", "*");

            context.result(response.toString()).status(response.getInt("rCode"));
        } catch (Exception ex) {
            throw new NetworkException(ex);
        }
    }

    public void sendError(int rCode) {
        if (context == null)
            return;

        try {
            context.header("Access-Control-Allow-Origin", "*");
            context.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            context.header("Access-Control-Allow-Headers", "*");
            context.header("Access-Control-Allow-Credentials", "true");
            context.header("Access-Control-Allow-Credentials-Header", "*");
            context.status(rCode);
        } catch (Exception ex) {
            throw new NetworkException(ex);
        }
    }

}
