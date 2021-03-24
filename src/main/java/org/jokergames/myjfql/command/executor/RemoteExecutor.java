package org.jokergames.myjfql.command.executor;

import io.javalin.http.Context;
import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.database.Column;
import org.jokergames.myjfql.encryption.Encryption;
import org.jokergames.myjfql.exception.CommandException;
import org.jokergames.myjfql.exception.NetworkException;
import org.jokergames.myjfql.server.util.ResponseBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Janick
 */

public class RemoteExecutor extends Executor {

    private final Context context;
    private final ResponseBuilder builder;

    private final Encryption.Protocol protocol;
    private final String encryptionKey;

    public RemoteExecutor(String name, Context context, Encryption.Protocol protocol, String encryptionKey) {
        super(name);
        this.context = context;
        this.builder = MyJFQL.getInstance().getServer().getResponseBuilder();
        this.protocol = protocol;
        this.encryptionKey = encryptionKey;
    }

    public void sendError(String s) {
        sendError(new CommandException(s));
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

            {

                switch (response.getInt("rCode")) {
                    case 500:
                        MyJFQL.getInstance().getConsole().logServerError(getName(), response.get("exception").toString());
                        break;
                    case 401:
                        MyJFQL.getInstance().getConsole().logServerWarning(getName(), response.get("exception").toString());
                        break;
                    default:
                        MyJFQL.getInstance().getConsole().logServerInfo(getName(), response.toString());
                        break;
                }

            }

            JSONObject jsonObject = new JSONObject();

            for (String key : response.keySet()) {
                if (key.equals("answer")) {
                    List<Object> answer = new ArrayList<>();

                    try {
                        List<String> columns = (ArrayList<String>) response.get(key);

                        for (String column : columns) {
                            answer.add(protocol.encrypt(column, encryptionKey));
                        }

                    } catch (Exception ex) {
                        List<Column> columns = (ArrayList<Column>) response.get(key);

                        for (Column column : columns) {
                            Column encryptedColumn = new Column();
                            encryptedColumn.setCreation(column.getCreation());

                            for (String name : column.getContent().keySet()) {
                                encryptedColumn.putContent(protocol.encrypt(name, encryptionKey), protocol.encrypt(column.getContent(name).toString(), encryptionKey));
                            }

                            answer.add(encryptedColumn);
                        }

                    }

                    jsonObject.put(key, answer);
                } else if (key.equals("structure")) {
                    JSONArray subJSONArray = new JSONArray();

                    if (response.get(key) instanceof String[]) {
                        String[] strings = (String[]) response.get(key);

                        for (int i = 0; i < strings.length; i++) {
                            subJSONArray.put(protocol.encrypt(strings[i], encryptionKey));
                        }
                    } else if (response.get(key) instanceof JSONArray) {
                        JSONArray jsonArray = response.getJSONArray(key);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            subJSONArray.put(protocol.encrypt(jsonArray.get(i).toString(), encryptionKey));
                        }
                    }

                    jsonObject.put(key, subJSONArray);
                } else if (key.equals("exception")) {
                    jsonObject.put(key, protocol.encrypt(response.get(key).toString(), encryptionKey));
                } else {
                    jsonObject.put(key, response.get(key));
                }

            }

            context.result(jsonObject.toString()).status(response.getInt("rCode"));
        } catch (Exception ex) {
            throw new NetworkException(ex);
        }
    }

}
