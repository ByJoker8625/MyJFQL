package org.jokergames.myjfql.command;

public abstract class CommandSender {

    private final String name;
    private final String address;

    public CommandSender(final String name, final String address) {
        this.name = name;
        this.address = address;
    }

    public abstract boolean hasPermission(final String permission);

    public abstract boolean isStaticDatabase();

    public abstract void sendError(final Object obj);

    public abstract void sendInfo(final Object obj);

    public abstract void sendForbidden();

    public abstract void sendSyntax();

    public abstract void sendSuccess();

    public abstract void sendAnswer(final Object obj, final Object structure);

    public abstract void send(final Object obj);

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
