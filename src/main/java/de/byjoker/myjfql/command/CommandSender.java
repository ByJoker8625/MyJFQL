package de.byjoker.myjfql.command;

import de.byjoker.myjfql.database.DatabaseAction;
import de.byjoker.myjfql.user.session.Session;

public abstract class CommandSender {

    private final String name;
    private final Session session;

    public CommandSender(String name, Session session) {
        this.name = name;
        this.session = session;
    }

    public abstract boolean allowed(String database, DatabaseAction action);

    public abstract void sendError(Object obj);

    public abstract void sendForbidden();

    public abstract void sendSyntax();

    public abstract void sendSuccess();

    public abstract void sendResult(Object obj, Object structure);

    public abstract void send(Object obj);

    public String getName() {
        return name;
    }

    public Session getSession() {
        return session;
    }
}
