package org.jokergames.myjfql.command;

import io.javalin.websocket.WsMessageContext;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.exception.CommandException;
import org.jokergames.myjfql.user.User;
import org.json.JSONObject;

public class RemoteCommandSender extends CommandSender {

    private final WsMessageContext context;
    private final User user;
    private int id;

    public RemoteCommandSender(final String name, final String address, final WsMessageContext context) {
        super(name, address);
        this.context = context;
        this.id = -1;
        this.user = MyJFQL.getInstance().getUserService().getUser(name);
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
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", ResponseType.REST);
        jsonObject.put("id", id);
        jsonObject.put("structure", structure);
        jsonObject.put("answer", obj);
        this.send(jsonObject);
    }

    @Override
    public void send(final Object obj) {
        if (!(obj instanceof JSONObject))
            throw new CommandException("Input is not a jsonobject!");
        context.send(((JSONObject) obj).toString());
    }

    public CommandSender toCommandSenderWithId(final int id) {
        final RemoteCommandSender sender = this;
        sender.setId(id);

        return sender;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
