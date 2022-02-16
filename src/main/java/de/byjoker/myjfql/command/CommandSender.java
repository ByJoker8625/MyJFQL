package de.byjoker.myjfql.command;

import de.byjoker.myjfql.database.DatabasePermissionLevel;
import de.byjoker.myjfql.database.TableEntry;
import de.byjoker.myjfql.network.session.Session;
import de.byjoker.myjfql.util.ResultType;

import java.util.Collection;

public abstract class CommandSender {

    private final String name;
    private final Session session;

    public CommandSender(String name, Session session) {
        this.name = name;
        this.session = session;
    }

    public abstract boolean allowed(String database, DatabasePermissionLevel action);

    public abstract void sendError(Object obj);

    public abstract void sendForbidden();

    public abstract void sendSyntax();

    public abstract void sendSuccess();

    public abstract void sendResult(Collection<TableEntry> entries, Collection<String> structure, ResultType resultType);

    public abstract void send(Object obj);

    public String getName() {
        return name;
    }

    public Session getSession() {
        return session;
    }
}
