package org.jokergames.myjfql.command;

import io.javalin.http.Context;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.exception.CommandException;
import org.jokergames.myjfql.user.User;
import org.json.JSONObject;

public class RemoteCommandSender extends CommandSender {

    private final Context context;
    private final User user;

    public RemoteCommandSender(final String name, final String address, final Context context) {
        super(name, address);
        this.user = MyJFQL.getInstance().getUserService().getUser(name);
        this.context = context;
    }

    @Override
    public boolean hasPermission(final String permission) {
        if (user == null)
            return false;

        return user.hasPermission(permission);
    }

    @Override
    public boolean isStaticDatabase() {
        return user.isStaticDatabase();
    }

    @Override
    public void sendError(final Object obj) {
        if (!(obj instanceof Exception) && !(obj instanceof String))
            throw new CommandException("Input must be an exception or a string!");

        final JSONObject jsonObject = new JSONObject();

        if (obj instanceof String) {
            jsonObject.put("exception", new CommandException(obj.toString()));
        } else {
            jsonObject.put("exception", obj);
        }

        jsonObject.put("type", ResponseType.ERROR);
        this.send(jsonObject);
    }

    @Deprecated
    @Override
    public void sendInfo(final Object obj) {
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
    public void sendAnswer(final Object obj, final Object structure) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", ResponseType.REST);
        jsonObject.put("structure", structure);
        jsonObject.put("answer", obj);
        this.send(jsonObject);
    }

    @Override
    public void send(final Object obj) {
        if (!(obj instanceof JSONObject))
            throw new CommandException("Input must be a JSONObject!");

        context.header("Access-Control-Allow-Origin", "*");
        context.header("Access-Control-Allow-Methods", "GET, POST");
        context.header("Access-Control-Allow-Headers", "*");
        context.header("Access-Control-Allow-Credentials", "true");
        context.header("Access-Control-Allow-Credentials-Header", "*");
        context.result(obj.toString());
    }

    public RemoteCommandSender rename(final String name) {
        return new RemoteCommandSender(name, getAddress(), context);
    }

    public enum ResponseType {
        ERROR,
        REST,
        FORBIDDEN,
        SUCCESS,
        SYNTAX_ERROR
    }
}
