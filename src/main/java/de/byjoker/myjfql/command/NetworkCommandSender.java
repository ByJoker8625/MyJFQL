package de.byjoker.myjfql.command;

import de.byjoker.myjfql.database.DatabasePermissionLevel;
import de.byjoker.myjfql.database.TableEntry;
import de.byjoker.myjfql.network.util.*;
import de.byjoker.myjfql.user.User;
import de.byjoker.myjfql.util.ResultType;

import java.util.Collection;

public class NetworkCommandSender extends CommandSender {

    private final Connection connection;
    private final User user;

    public NetworkCommandSender(User user, Connection connection) {
        super(user.getName(), connection.getSession());
        this.connection = connection;
        this.user = user;
    }

    @Override
    public boolean allowed(String database, DatabasePermissionLevel action) {
        return user.allowed(database, action);
    }

    @Override
    public void sendError(Object obj) {
        connection.respond(new ErrorResponse(obj.toString()));
    }

    @Override
    public void sendForbidden() {
        connection.respond(new Response(ResponseType.FORBIDDEN));
    }

    @Override
    public void sendSyntax() {
        connection.respond(new ErrorResponse("Unknown syntax!"));
    }

    @Override
    public void sendSuccess() {
        connection.respond(new Response(ResponseType.SUCCESS));
    }

    @Override
    public void sendResult(Collection<TableEntry> entries, Collection<String> structure, ResultType resultType) {
        connection.respond(new Result(entries, structure, resultType));
    }

    @Override
    public void send(Object obj) {
        connection.respond((Response) obj);
    }
}
