package de.byjoker.myjfql.command;

import de.byjoker.myjfql.database.DatabasePermissionLevel;
import de.byjoker.myjfql.database.TableEntry;
import de.byjoker.myjfql.network.session.Session;
import de.byjoker.myjfql.network.util.*;
import de.byjoker.myjfql.user.User;
import de.byjoker.myjfql.util.ResultType;

import java.util.Collection;

public class LegacyNetworkCommandSender extends CommandSender {

    private final User user;
    private final Connection connection;
    private final Session session;

    public LegacyNetworkCommandSender(User user, Connection connection, Session session) {
        super(user.getName(), session);
        this.user = user;
        this.connection = connection;
        this.session = session;
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
