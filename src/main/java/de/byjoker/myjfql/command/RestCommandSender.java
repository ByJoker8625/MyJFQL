package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.TableEntry;
import de.byjoker.myjfql.database.DatabaseActionPerformType;
import de.byjoker.myjfql.exception.LanguageException;
import de.byjoker.myjfql.server.session.Session;
import de.byjoker.myjfql.user.User;
import de.byjoker.myjfql.util.TableEntryParser;
import de.byjoker.myjfql.util.ResultType;
import io.javalin.http.Context;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class RestCommandSender extends CommandSender {

    private final Context context;
    private final User user;

    public RestCommandSender(Session session, Context context) {
        super(null, session);
        this.user = (session == null) ? null : session.getUser(MyJFQL.getInstance().getUserService());
        this.context = context;
    }

    @Override
    public boolean allowed(String database, DatabaseActionPerformType action) {
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
    public void sendResult(Collection<TableEntry> entries, Collection<String> structure, ResultType resultType) {
        send(TableEntryParser.stringifyTableEntries(entries, structure, resultType));
    }

    @Override
    public void send(Object obj) {
        sendBytes(obj.toString().getBytes(StandardCharsets.UTF_8));
    }

    public void sendBytes(byte[] bytes) {
        context.header("Content-Type", "application/json");
        context.result(bytes);
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
