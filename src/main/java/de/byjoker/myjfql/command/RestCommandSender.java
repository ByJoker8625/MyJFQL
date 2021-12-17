package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.DatabaseAction;
import de.byjoker.myjfql.exception.LanguageException;
import de.byjoker.myjfql.user.User;
import de.byjoker.myjfql.user.session.Session;
import io.javalin.http.Context;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class RestCommandSender extends CommandSender {

    private final Context context;
    private final User user;

    public RestCommandSender(Session session, Context context) {
        super(null, session);
        this.user = (session == null) ? null : session.getUser(MyJFQL.getInstance().getUserService());
        this.context = context;
    }

    @Override
    public boolean allowed(String database, DatabaseAction action) {
        return user.allowed(database, action);
    }

    @Override
    public void sendError(Object obj) {
        final JSONObject jsonObject = new JSONObject();

        if (obj instanceof String) {
            jsonObject.put("exception", new LanguageException(obj.toString()));
        } else {
            jsonObject.put("exception", obj);
        }

        jsonObject.put("type", ResponseType.ERROR);
        this.send(jsonObject);
    }

    @Override
    public void sendForbidden() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", ResponseType.FORBIDDEN);
        this.send(jsonObject);
    }

    @Override
    public void sendSyntax() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", ResponseType.SYNTAX_ERROR);
        this.send(jsonObject);
    }

    @Override
    public void sendSuccess() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", ResponseType.SUCCESS);
        this.send(jsonObject);
    }

    @Override
    public void sendResult(final Object obj, final Object structure) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", ResponseType.RESULT);
        jsonObject.put("structure", structure);
        jsonObject.put("result", obj);
        this.send(jsonObject);
    }

    @Override
    public void send(final Object obj) {
        context.header("Access-Control-Allow-Origin", "*");
        context.header("Access-Control-Allow-Methods", "GET, POST");
        context.header("Access-Control-Allow-Headers", "*");
        context.header("Access-Control-Allow-Credentials", "true");
        context.header("Access-Control-Allow-Credentials-Header", "*");
        context.header("Content-Type", "application/json");

        context.result(obj.toString().getBytes(StandardCharsets.UTF_8));
    }

    public RestCommandSender bind(Session session) {
        return new RestCommandSender(session, context);
    }

    public String getName() {
        if (user == null)
            return null;

        return user.getName();
    }

    public enum ResponseType {
        ERROR,
        RESULT,
        FORBIDDEN,
        SUCCESS,
        SYNTAX_ERROR
    }
}
