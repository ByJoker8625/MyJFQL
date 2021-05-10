package org.jokergames.myjfql.command;

import io.javalin.http.Context;
import io.javalin.websocket.WsMessageContext;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.database.Column;
import org.jokergames.myjfql.exception.CommandException;
import org.jokergames.myjfql.user.User;
import org.jokergames.myjfql.util.ConditionHelper;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class RemoteCommandSender extends CommandSender {

    private final WsMessageContext socketContext;
    private final Context httpContext;
    private final User user;
    private String id;

    public RemoteCommandSender(final String name, final String address, final WsMessageContext socketContext, final Context httpContext) {
        super(name, address);
        this.socketContext = socketContext;
        this.id = "-1";
        this.user = MyJFQL.getInstance().getUserService().getUser(name);
        this.httpContext = httpContext;
    }

    @Override
    public boolean hasPermission(final String permission) {
        if (user == null)
            return false;
        else
            return user.hasPermission(permission);
    }

    @Override
    public boolean isStaticDatabase() {
        return user.isStaticDatabase();
    }

    @Override
    public void sendError(final Object obj) {
        if (!(obj instanceof Exception) && !(obj instanceof String))
            throw new CommandException("Input is not a string or exception!");

        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);

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
        jsonObject.put("id", id);
        this.send(jsonObject);
    }

    @Override
    public void sendSyntax() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", ResponseType.SYNTAX_ERROR);
        jsonObject.put("id", id);
        this.send(jsonObject);
    }

    @Override
    public void sendSuccess() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", ResponseType.SUCCESS);
        jsonObject.put("id", id);
        this.send(jsonObject);
    }

    @Override
    public void sendAnswer(final Object obj, final Object structure) {
        if (!(obj instanceof List))
            throw new CommandException("Input isn't a list!");

        if (!(structure instanceof String[]) && !(structure instanceof List))
            throw new CommandException("Input is not an array!");

        long l = System.currentTimeMillis();

        List<String> values = null;

        if (!(structure instanceof String[])) {
            values = (List<String>) structure;
        } else {
            values = Arrays.asList((String[]) structure);
        }

        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", ResponseType.REST);
        jsonObject.put("id", id);
        jsonObject.put("structure", values);

        try {
            jsonObject.put("answer", ConditionHelper.getRequiredColumnRows((List<Column>) obj, values));
        } catch (Exception ex) {
            jsonObject.put("answer", obj);
        }

        this.send(jsonObject);
    }

    @Override
    public void send(final Object obj) {
        if (!(obj instanceof JSONObject))
            throw new CommandException("Input is not a jsonobject!");
        if (httpContext == null)
            socketContext.send(((JSONObject) obj).toString());
        else {
            httpContext.header("Access-Control-Allow-Origin", "*");
            httpContext.header("Access-Control-Allow-Methods", "GET,POST");
            httpContext.header("Access-Control-Allow-Headers", "*");
            httpContext.header("Access-Control-Allow-Credentials", "true");
            httpContext.header("Access-Control-Allow-Credentials-Header", "*");
            httpContext.result(((JSONObject) obj).toString());
        }
    }

    public CommandSender toCommandSenderWithId(final String id) {
        final RemoteCommandSender sender = this;
        sender.setId(id);

        return sender;
    }


    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public enum ResponseType {
        ERROR,
        REST,
        FORBIDDEN,
        SUCCESS,
        SYNTAX_ERROR
    }
}
